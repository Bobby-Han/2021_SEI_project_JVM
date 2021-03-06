package edu.nju;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

public class ClassFileReader {
    private static final String FILE_SEPARATOR = File.separator;
    private static final String PATH_SEPARATOR = File.pathSeparator;

    private static Entry bootStrapReader;

    public static Entry chooseEntryType(String classpath) {
        /**
         * tips:
         *      Every case can correspond to a class
         *      These cases are disordered
         *      You should take care of the order of if-else
         * case 1 classpath with wildcard
         * case 2 normal dir path
         * case 3 archived file
         * case 4 classpath with path separator
         */
        if(classpath.contains("*")&&(!classpath.contains(PATH_SEPARATOR))){
            bootStrapReader=new WildEntry(classpath);
        }else if(classpath.contains(PATH_SEPARATOR)){
            bootStrapReader=new CompositeEntry(classpath);
        }else if((StringUtils.containsIgnoreCase(classpath,"jar")||StringUtils.containsIgnoreCase(classpath,"zip"))&&(!classpath.contains(PATH_SEPARATOR))){
            bootStrapReader=new ArchivedEntry(classpath);
        }else {
            bootStrapReader=new DirEntry(classpath);
        }
        return bootStrapReader;
    }

    /**
     *
     * @param classpath where to find target class
     * @param className format: /package/.../class
     * @return content of classfile
     */
    public static byte[] readClassFile(String classpath,String className) throws ClassNotFoundException{
        byte[] ret = new byte[0];
        if(className==null){
            ret=null;
        }else {
            className = IOUtil.transform(className);
            className += ".class";
            bootStrapReader = chooseEntryType(classpath);

            try {
                ret = bootStrapReader.readClassFile(className);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (ret==null)throw new ClassNotFoundException();
        return ret;
    }
}
