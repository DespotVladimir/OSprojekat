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

        while(true){
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
            String address=hm.findFirstEmpty();
            File file = new File(fileName,content,address);
            fsm.addFile(filePath, file);
            hm.writeToDisk(content);

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

        System.out.println("Finished booting. \n");
    }

    public Process createProcess(String code)
    {
        Process process = new Process(pm.getFreeID(),code);
        pm.addProcess(process);
        mm.allocateMemory(process);

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
            int newBlock = Assembly.binaryToDecimal(Assembly.blockToString(temp).substring(4));
            Page jumpPage = page;
            if(newBlock>=Page.pageSize/Assembly.CodeBlockSize*page.getPageNumber()){
                jumpPage = findPage(p,(int)(newBlock/(Page.pageSize/Assembly.CodeBlockSize)));
            }
            switch (command) {
                case "LDA" -> {
                    cpu.LDA(jumpPage, number);
                    p.incrementBlock();
                }
                case "JMP" -> cpu.JMP(p, newBlock);
                case "JZ" -> cpu.JZ(p, newBlock);
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

    public String getHDDContent(String hddAddress){
        return hm.readFromDisk(hddAddress);
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

    public String getCurrentDirectory(){
        return currentDirectory.getName();
    }

    // TERMINAL COMMANDS
    public void rd(String fileName)
    {
        String name=fileName;
        Directory dir = currentDirectory;
        if(FileSystemManager.isAbsolute(fileName)){
            dir = fsm.navigateToDirectory(fileName.substring(0,fileName.lastIndexOf("/")));
            name = fileName.substring(fileName.lastIndexOf("/")+1);
        }
        String address = dir.getFileAddress(name);

        System.out.println(getHDDContent(address));
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
        System.out.println("Used memory: "+percentOfUsed+"%");
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
