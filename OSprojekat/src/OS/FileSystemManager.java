package OS;

public class FileSystemManager {
    private Directory rootDirectory;

    public FileSystemManager() {
        rootDirectory = new Directory("root");
        rootDirectory.setCurrentDirectory("root");
    }

    public void addFile(String path,File File){
        Directory directory = navigateToDirectory(path);
        directory.addFile(File);
    }

    public void addDirectory(String path,Directory d){
        Directory directory = navigateToDirectory(path);
        directory.addDirectory(d);
    }

    public void addDirectoryWithFullPath(String pathWithName){
        String path = pathWithName.substring(0,pathWithName.lastIndexOf("/"));
        String name = pathWithName.substring(pathWithName.lastIndexOf("/")+1);
        addDirectory(path,new Directory(name,pathWithName));
    }

    public Directory navigateToDirectory(String path){
        String[] dirs = path.split("/");

        Directory currentDirectory = rootDirectory;
        for(int i = 1; i < dirs.length; i++){
            currentDirectory = currentDirectory.getDirectory(dirs[i]);
            if(currentDirectory == null) return null;
        }
        return currentDirectory;
    }

    public Directory findDirectory(String name){
        return findDirectory(name,rootDirectory);
    }

    public static boolean isAbsolute(String path){
        return path.startsWith("root/");
    }

    private Directory findDirectory(String name,Directory d){
        if(d == null || d.getName().equals(name))
            return d;

        Directory[] allDir = d.getAllDirectories();
        Directory result = null;

        for(Directory dir: allDir)
        {
            result = findDirectory(name,dir);
            if(result != null)
                return result;
        }
        return result;
    }

    public void printAllDirectories(){
        printRecursive(rootDirectory,0);
    }

    private void printRecursive(Directory dir,int tabs){
        for (int i = 0; i < tabs; i++)
            System.out.print("  ");
        System.out.print("/");
        System.out.println(dir.getName());
        for(String fileAddresses: dir.getAllFilesNamesAddress())
        {
            for (int i = 0; i < tabs; i++)
                System.out.print("  ");

            System.out.print(" ");
            String name = fileAddresses.split("&")[0];
            System.out.println(name);
        }
        for(Directory child: dir.getAllDirectories())
        {
            printRecursive(child,tabs+1);
        }
    }

}
