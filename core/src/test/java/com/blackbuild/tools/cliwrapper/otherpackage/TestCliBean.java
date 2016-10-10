package com.blackbuild.tools.cliwrapper.otherpackage;

import com.blackbuild.tools.cliwrapper.ArgumentType;
import com.blackbuild.tools.cliwrapper.CliArgsParameter;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * TODO Replace with class description.
 */
@SuppressWarnings("UnusedDeclaration")
public class TestCliBean  {

    @CliArgsParameter
    private String string;

    @CliArgsParameter(type = ArgumentType.DEFAULT)
    private String defaultarg;

    @CliArgsParameter(argument="override")
    private String renamed;

    @CliArgsParameter
    private File file;

    @CliArgsParameter
    private String[] array;

    @CliArgsParameter
    private List<String> list;

    @CliArgsParameter(delimiter=",")
    private List<String> commaList;

    @CliArgsParameter(delimiter=" ")
    private List<String> spaceList;

    @CliArgsParameter
    private List<File> filelist;

    @CliArgsParameter
    private Map<String, String> map;

    @CliArgsParameter(assignment=" ")
    private Map<String, String> spaceAssignedMap;


    public String getString() {
        return this.string;
    }


    public void setString(String string) {
        this.string = string;
    }


    public String getRenamed() {
        return this.renamed;
    }


    public void setRenamed(String renamed) {
        this.renamed = renamed;
    }


    public File getFile() {
        return this.file;
    }


    public void setFile(File file) {
        this.file = file;
    }


    public String[] getArray() {
        return this.array;
    }


    public void setArray(String[] array) {
        this.array = array;
    }


    public List<File> getFilelist() {
        return this.filelist;
    }


    public void setFilelist(List<File> filelist) {
        this.filelist = filelist;
    }


    public Map<String, String> getMap() {
        return this.map;
    }


    public void setMap(Map<String, String> map) {
        this.map = map;
    }


    public String getDefaultarg() {
        return this.defaultarg;
    }


    public void setDefaultarg(String defaultarg) {
        this.defaultarg = defaultarg;
    }


    public Map<String, String> getSpaceAssignedMap() {
        return this.spaceAssignedMap;
    }


    public void setSpaceAssignedMap(Map<String, String> spaceAssignedMap) {
        this.spaceAssignedMap = spaceAssignedMap;
    }


    public List<String> getList() {
        return this.list;
    }


    public void setList(List<String> list) {
        this.list = list;
    }


    public List<String> getCommaList() {
        return this.commaList;
    }


    public void setCommaList(List<String> commaList) {
        this.commaList = commaList;
    }


    public List<String> getSpaceList() {
        return this.spaceList;
    }


    public void setSpaceList(List<String> spaceList) {
        this.spaceList = spaceList;
    }



}
