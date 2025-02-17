package OS;

import java.util.ArrayList;

public class Directory {
    private String name;

    private ArrayList<String> files;     //name&address
    private ArrayList<Directory> listDirectory;

    private String previousDirectory;
    private String currentDirectory;

    Directory(String name) {
        this.name = name;
        files = new ArrayList<>();
        listDirectory = new ArrayList<>();
    }

    Directory(String name, String currentDirectory) {
        this.name = name;
        files = new ArrayList<>();
        listDirectory = new ArrayList<>();
        this.currentDirectory = currentDirectory;
        this.previousDirectory = currentDirectory.substring(0, currentDirectory.lastIndexOf("/"));
    }

    public Directory() {

    }

    public void addDirectory(Directory directory) {
        listDirectory.add(directory);
    }

    public void removeDirectory(Directory directory) {
        listDirectory.remove(directory);
    }

    public void removeDirectory(String name){
        for(Directory directory : listDirectory){
            removeDirectory(directory);
            break;
        }
    }

    public String removeFile(String fileName) {
        String temp = null;
        for(int i=0; i<files.size(); i++){
            if((files.get(i).split("&")[0]).equals(fileName)){
                temp = files.get(i);
                files.remove(i);
                break;
            }
        }
        return temp;
    }

    public boolean hasFile(String fileName) {
        for(String file:files){
            if((file.split("&")[0]).equals(fileName)){
                return true;
            }
        }
        return false;
    }


    public void addFile(File file) {
        files.add(file.getName()+"&"+file.getAddress());
    }

    public Directory getDirectory(String name) {
        for(Directory directory : listDirectory){
            if(directory.getName().equals(name)){
                return directory;
            }
        }
        return null;
    }

    public String getFileAddress(String fileName) {
        for(String file:files){
            if((file.split("&")[0]).equals(fileName)){
                return file.split("&")[1];
            }
        }
        return null;
    }

    public String[] getAllFilesNamesAddress(){
        return files.toArray(new String[0]);
    }

    public Directory[] getAllDirectories(){
        return listDirectory.toArray(new Directory[0]);
    }

    public String getName() {
        return name;
    }

    public String getPreviousDirectory() {
        return previousDirectory;
    }

    public String getCurrentDirectory() {
        return currentDirectory;
    }

    public void printAll(){
        System.out.println("Directory {" + name + "}: " );
        for(Directory directory : listDirectory){
            System.out.println("/"+directory.getName());
        }
        for(String file : files){
            System.out.println(file.split("&")[0]);
        }
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setCurrentDirectory(String currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    @Override
    public String toString() {
        return getName();
    }
}
