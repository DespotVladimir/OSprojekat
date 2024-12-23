import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class Kernel extends Thread {
    private final CPU cpu;
    private final FileSystemManager fsm;
    private final ProcessManager pm;
    private final MemoryManager mm;
    private final HDDManager hm;


    private Directory currentDirectory;
    private HashMap<Process, ArrayList<String>> page_table;


    public Kernel() {
        cpu = new CPU();
        fsm = new FileSystemManager();
        pm = new ProcessManager();
        mm = new MemoryManager();
        hm = new HDDManager();

        currentDirectory = fsm.navigateToDirectory("root");

        page_table = new HashMap<>();

    }

    public void start(){
        System.out.println("Starting kernel");

        Process currentProcess = pm.scheduleNextProcess();;


        long startTime = System.currentTimeMillis();
        while(true){
            long now = System.currentTimeMillis();
            if(cpu.getClock()>=CPU.clockCycle)
            {
                if(now-startTime < CPU.clockCycle) {
                    continue;
                }
                else {
                    cpu.resetClock();
                    startTime = System.currentTimeMillis();
                }
            }

            if(cpu.isNextCycle())
            {
                currentProcess.setCpuAccumulator(cpu.getAC());
                currentProcess = pm.scheduleNextProcess();
                cpu.getRegister("AC").setValue(currentProcess.getCpuAccumulator());
            }
            if(currentProcess.getState()==ProcessState.TERMINATED)
                currentProcess = pm.scheduleNextProcess();
            if(pm.hasProcessToTerminate())
                for(Process p : pm.getProcessesToTerminate())
                    mm.freeMemory(p);
            executeNextCommand(currentProcess);
        }
    }

    public void boot()throws Exception {
        System.out.println("Booting...");
        System.out.println("Opening boot file");
        Path path = Path.of("boot.file");
        String[] bootLines = Files.readAllLines(path).toArray(new String[0]);

        System.out.println("Adding directories");
        //Directories
        String[] dirs = Hex.fromHex(bootLines[0]).split(";");
        for(String dir:dirs){
            fsm.addDirectoryWithFullPath(dir);
        }

        System.out.println("Adding files");
        //Files
        java.io.File fileDir = new java.io.File(Hex.fromHex(bootLines[1]));
        java.io.File[] fileNames = fileDir.listFiles();
        for(java.io.File filer: fileNames) {
            String fileName = filer.getName();
            String[] fileLines = Files.readAllLines(Path.of("files/" + fileName)).toArray(new String[0]);
            String filePath=fileLines[0];
            StringBuilder contentSB = new StringBuilder();
            for (int i = 1; i < fileLines.length; i++) {
                contentSB.append(fileLines[i]);
                contentSB.append("\n");
            }
            String content=contentSB.toString();
            createFile(fileName,filePath, content);
        }

        System.out.println("Adding system processes");
        //Procesi
        String processLine = Hex.fromHex(bootLines[2]);
        String[] processPaths = processLine.split(";");
        for(String processPath:processPaths){
            Directory dir = fsm.navigateToDirectory(processPath.substring(0,processPath.lastIndexOf("/")));
            String name = processPath.substring(processPath.lastIndexOf("/")+1);
            String fileContents = hm.readFromDisk(dir.getFileAddress(name));
            Process p = createProcess(fileContents);
            p.setName(name);
        }
        for(int i=0;i<120;i++)
        {
            String ProcessPath="root/sys";
            Directory dir = fsm.navigateToDirectory(ProcessPath);
            String name="sysos.file";
            String fileContents = hm.readFromDisk(dir.getFileAddress(name));
            Process p = createProcess(fileContents);
            p.setName(name);
        }

        System.out.println("Finished booting. \n");
    }


    public void executeNextCommand(Process p)
    {
        int block = p.getPageBlock();
        int numOfPages = p.getPageNumber();

        Page page;
        try{
            page = findPage(p,numOfPages);

        }catch (Exception _){
            // TODO Nemam pojma sto ovo ne radi, ako prekines ovdje radi normalno sljedeci put
            return;
        }
        Byte[] temp = page.getMemoryBlock(block);

        String command = Assembly.opcode_table.get(Assembly.blockToString(temp).substring(0,4));
        String number = Assembly.blockToString(temp).substring(4);


        if(Assembly.isJumper(command))
        {

            // newBlock gleda da li prelazi u sljedeci page neki pa taj page trazi
            int NumberOfBlocks = Page.pageSize/Assembly.CodeBlockSize;

            int newBlock = Assembly.binaryToDecimal(Assembly.blockToString(temp).substring(4));
            Page jumpPage = page;

            if(newBlock<NumberOfBlocks*page.getPageNumber()) {
                jumpPage = findPage(p,(int)(newBlock/NumberOfBlocks));
            }
            else if(newBlock>=NumberOfBlocks*page.getPageNumber()+NumberOfBlocks){
                jumpPage = findPage(p,(int)(newBlock/(NumberOfBlocks)));
            }
            switch (command) {
                case "LDA" -> {
                    cpu.LDA(jumpPage, number);
                    p.incrementBlock();
                }
                case "JMP" -> cpu.JMP(p, newBlock);
                case "JZ" -> cpu.JZ(p, newBlock);
                case "JNZ" -> cpu.JZN(p, newBlock);
                case "STA" -> {
                    cpu.STA(jumpPage, number);
                    p.incrementBlock();
                }
            }
        }else{
            switch (command) {
                case "HLT" -> {
                    cpu.HLT();
                    p.terminate();
                }
                case "ADD" -> {
                    cpu.ADD(number);
                    p.incrementBlock();
                }
                case "SUB" -> {
                    cpu.SUB(number);
                    p.incrementBlock();
                }
            }
        }
    }

    public Process createProcess(String code)
    {
        Process process = new Process(pm.getFreeID(),code);
        pm.addProcess(process);
        Page lruPage = mm.allocateMemory(process);

        if(lruPage!=null) // znaci da je neka iz rama ispisana
        {
            int lruPID = lruPage.getProcessId();
            String emptyMemory = hm.findFirstEmptyVirtualMemory();
            hm.writePage(lruPage,emptyMemory);
            page_table.get(pm.getProcess(lruPID)).add(emptyMemory);
        }

        page_table.put(process, new ArrayList<>());
        for(Page page: process.getPages()){
            if(!page.isInMemory())
            {
                String address = hm.findFirstEmptyVirtualMemory();
                hm.writePage(page, address);
                page_table.get(process).add(address);
            }
        }
        return process;
    }

    public Page findPage(Process process,int pageNumber)
    {
        // Gleda u ramu, pa na hddu da li ima page

        Frame f = mm.getRam().getFrameForPage(process.getID(),pageNumber);

        if(f==null)
        {
            ArrayList<String> addresses = page_table.get(process);
            for(String address:addresses){
                Page re = hm.getPage(address,process.getID());
                if(re.getPageNumber()==pageNumber)
                {
                    return re;
                }

            }
        }


        return f.getPage();
    }

    public void createFile(String name,String path,String content)
    {
        String address = hm.findFirstEmpty();
        File file = new File(name,content,address);
        fsm.addFile(path, file);
        hm.writeToDisk(content);
    }

    public String getHDDContent(String hddAddress){
        return hm.readFromDisk(hddAddress);
    }

    public File getFile(String fileName){
        Directory dir = currentDirectory;
        String name= fileName;
        if(FileSystemManager.isAbsolute(fileName))
        {
            dir = fsm.navigateToDirectory(fileName.substring(0,fileName.lastIndexOf("/")));
            name = fileName.substring(fileName.lastIndexOf("/")+1);
        }
        String address = dir.getFileAddress(name);
        String content = hm.readFromDisk(address);
        return new File(name,content,address);
    }

    public String getFileContents(String filePath){
        // Sadrzaj fajla pomocu putanje direktorijuma

        Directory dir = currentDirectory;
        String filename;
        if(FileSystemManager.isAbsolute(filePath)){
            String dir_path = filePath.substring(0,filePath.lastIndexOf("/"));
            filename = filePath.substring(filePath.lastIndexOf("/")+1);
            dir = fsm.navigateToDirectory(dir_path);
        }
        else {
            filename = filePath;
        }
        String fileAddress = dir.getFileAddress(filename);
        return getHDDContent(fileAddress);
    }

    public void updateFileContent(String fileName,String newContent)
    {
        File file = getFile(fileName);
        hm.writeToDisk(file.getAddress(),newContent);
    }

    public String getCurrentDirectory(){
        return currentDirectory.getName();
    }

    // TERMINAL COMMANDS
    public void rd(String fileName)
    {
        String name = fileName;
        Directory dir = currentDirectory;
        if(FileSystemManager.isAbsolute(fileName)){
            dir = fsm.navigateToDirectory(fileName.substring(0,fileName.lastIndexOf("/")));
            name = fileName.substring(fileName.lastIndexOf("/")+1);
        }
        String address = dir.getFileAddress(name);

        System.out.println(getHDDContent(address));
    }

    public void opn(String fileName,String content)
    {
        updateFileContent(fileName,content);
    }

    public void cd(String path) {
        boolean isAbsolute = FileSystemManager.isAbsolute(path);
        if(isAbsolute)
        {
            System.out.println("Moving to: "+ path);
            Directory dir=fsm.navigateToDirectory(path);
            if(dir==null)
                System.out.println("Directory not found. ");
            else
                currentDirectory = dir;
        }
        else{
            Directory dir = currentDirectory.getDirectory(path);
            if(dir==null)
                System.out.println("Directory not found. ");
            else
                currentDirectory = dir;
        }
    }

    public void dir(){
        currentDirectory.printAll();
    }

    public void dirs(){
        fsm.printAllDirectories();
    }

    public void ps(){
        pm.printAll();
    }

    public void mem(){
        double percentOfUsed = mm.getUsedMemory() * 100;
        int frameCount = mm.getRam().getFrameCount();
        int remSpace = mm.getRam().remainingSpace();
        System.out.println("Used memory: "+percentOfUsed+"%, "+(Page.pageSize*(frameCount-remSpace))+"/"+frameCount*Page.pageSize);
    }

    public void rm(String file){
        String s = currentDirectory.removeFile(file);
        if(s==null)
            System.out.println("File " + file + " not found in current directory. ");
        else
        {
            hm.writeToDisk(s.split("&")[1],null);
            System.out.println("File "+file+" removed.");
        }
    }

    public void mkdir(String dir_name) {
        Directory dir = new Directory(dir_name);
        currentDirectory.addDirectory(dir);
    }

    public void mkfile(String fileName)
    {
        Directory dir = currentDirectory;
        String name = fileName;
        if(FileSystemManager.isAbsolute(fileName))
        {
            dir = fsm.navigateToDirectory(fileName.substring(0,fileName.lastIndexOf("/")));
            name = fileName.substring(fileName.lastIndexOf("/")+1);
        }
        createFile(name,dir.getCurrentDirectory(),"");

    }

    public void hddPrint()
    {
        hm.getHDD().printUsedMemory();
    }

    public void block(String PID)
    {
        int pid = Integer.parseInt(PID);
        Process p = pm.getProcess(pid);
        if(p==null)
            System.out.println("Process not found. ");
        else
            p.block();
    }

    public void unblock(String PID)
    {
        int pid = Integer.parseInt(PID);
        Process p = pm.getProcess(pid);
        if(p==null)
            System.out.println("Process not found. ");
        else
            p.unblock();
    }

    public void runs(String path){
        String fileContent = getFileContents(path);
        int nameIndex = path.lastIndexOf("/")+1;
        Process p = createProcess(fileContent);
        if(nameIndex==0)
            p.setName(path);
        else
            p.setName(path.substring(nameIndex));
    }



    @Override
    public void run() {
        start();
    }
}
