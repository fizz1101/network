package com.quanwei.network.core;

import java.io.*;
import java.util.*;

public class FileUtil {

    /**
     * 读取文件内容
     * @date 2017年1月11日
     * @author 张纯真
     * @param filePath
     * @param encoding
     * @return
     */
    public static List<List<String>> readFile(String filePath, String separator, String encoding) {
        List<List<String>> list_txt = new ArrayList<List<String>>();
        BufferedReader bufferedReader = null;
        try {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);//考虑到编码格式
                bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                    List<String> list_cell = new ArrayList<String>();
                    if (separator != null) {
                        String[] cellArr = lineTxt.split(separator);
                        for (int i=0; i<cellArr.length; i++) {
                            String content = cellArr[i];
                            if (content != null && !"".equals(content)) {
                                list_cell.add(cellArr[i].trim());
                            }
                        }
                    } else {
                        list_cell.add(lineTxt);
                    }
                    list_txt.add(list_cell);
                }
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return list_txt;
    }

    /**
     * 写数据入文件
     * @date 2017年10月17日
     * @author 张纯真
     * @param filePath
     * @param fileName
     * @param content
     * @return
     */
    public static boolean writeFile(BufferedWriter writer, String filePath, String fileName, String content) {
        boolean flag = false;
        boolean flag_bw = false;
        try {
            File folder = new File(filePath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            if (writer == null) {
                writer = new BufferedWriter(new FileWriter(new File(filePath + fileName), true));
                flag_bw = true;
            }
            writer.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (flag_bw && writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    /**
     * 替换文件内容
     * @param filePath 文件路径
     * @param replaceMap 替换内容
     * @return
     */
    public static boolean replaceContent(String filePath, Map<String, String> replaceMap) {
        boolean flag = false;
        File file = new File(filePath);
        Long fileLength = file.length();
        byte[] fileContext = new byte[fileLength.intValue()];
        FileInputStream in = null;
        PrintWriter out = null;
        try {
            in = new FileInputStream(filePath);
            in.read(fileContext);
            // 避免出现中文乱码
            String str = new String(fileContext, "utf-8");
            for (String key : replaceMap.keySet()) {
                str = str.replaceAll(key, replaceMap.get(key));
            }
            out = new PrintWriter(filePath);
            out.write(str);
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.flush();
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    public static boolean replaceContent(String filePath, String fileName, String oldStrRegex, String newStr) {
        boolean flag = false;
        try {
            File folder = new File(filePath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File file = new File(filePath+File.separator+fileName);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            // 内存流, 作为临时流
            CharArrayWriter  tempStream = new CharArrayWriter();
            String line;
            while ((line = reader.readLine()) != null) {
                // 替换每行中, 符合条件的字符串
                line.replaceAll(oldStrRegex, newStr);
                // 将该行写入内存
                tempStream.write(line);
                // 添加换行符
                tempStream.append(System.getProperty("line.separator"));
            }
            // 关闭 输入流
            reader.close();
            // 将内存中的流 写入 文件
            FileWriter writer = new FileWriter(file);
            tempStream.writeTo(writer);
            writer.flush();
            writer.close();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public static boolean replace(String filePath, String fileName, String oldStrRegex, String newStr) {
        boolean flag = false;
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(filePath+File.separator+fileName, "rw");
            String line;
            //上一次的偏移量
            long lastPoint = 0;
            while ((line = raf.readLine()) != null) {
                final long point = raf.getFilePointer();
                if (line.contains(oldStrRegex)) {
                    String str = line.replaceAll(oldStrRegex, newStr);
                    raf.seek(lastPoint);
                    raf.writeBytes(str);
                }
                lastPoint = point;
            }
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    /**
     * 获取目录下的文件(根据文件名)
     * @param path 路径
     * @param fileNameRegex 文件名正则
     * @param extraFileRegex 排除的文件名正则
     * @param order 排序(0:不排序;1:升序;2:降序;)
     * @return
     */
    public static List<File> getFileListByName(String path, String fileNameRegex, String extraFileRegex, int order) {
        List<File> list_res = new ArrayList<File>();
        List<File> list_file = new ArrayList<File>();
        File file_path = new File(path);
        File[] files = file_path.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isFile()) {
                    list_file.add(f);
                }
            }
            for (File file : list_file) {
                String fileName = file.getName();
                if (fileName.contains(fileNameRegex) && !fileName.contains(extraFileRegex)) {
                    list_res.add(file);
                }
            }
            list_res = orderByName(list_res, order);
        }
        return list_res;
    }

    /**
     * 获取目录下的文件(根据文件名)
     * @date 2017年9月30日
     * @author 张纯真
     * @param path
     * @param fileNames
     * @param order 0:不排序;1:升序;2:降序;
     * @return
     */
    public static List<File> getFileListByName(String path, List<String> fileNames, int order) {
        List<File> list_res = new ArrayList<File>();
        List<File> list_file = new ArrayList<File>();
        int size = fileNames.size();
        File file_path = new File(path);
        File[] files = file_path.listFiles();
        if (files != null) {
            int count = 0;
            for (File f : files) {
                if (f.isFile()) {
                    list_file.add(f);
                }
            }
            list_file = orderByName(list_file, order);
            for (File file : list_file) {
                String fileName = file.getName();
                if (fileNames.contains(fileName)) {
                    list_res.add(file);
                    count++;
                }
                if (count == size) {
                    break;
                }
            }
        }
        return list_res;
    }

    /**
     * 获取目录下的文件并排序
     * @date 2017年1月13日
     * @author 张纯真
     * @param path
     * @param extName
     * @param order 0:不排序;1:升序;2:降序;
     * @return
     */
    public static List<File> getFileListSort(String path, String extName, int order, int limit) {
        List<File> list_file = new ArrayList<File>();
        File file = new File(path);
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isFile() && f.getName().endsWith(extName)) {
                    list_file.add(f);
                }
            }
        }
        list_file = orderByName(list_file, order);
        if (limit > 0 && list_file.size()>limit) {
            list_file = list_file.subList(0, limit);
        }
        return list_file;
    }

    /**
     * 移动文件到目标目录
     * @date 2017年1月20日
     * @author 张纯真
     * @param file
     * @param destPath
     * @param newName
     * @return
     */
    public static boolean moveFile(File file, String destPath, String newName) {
        boolean flag = false;
        File folder = new File(destPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        if (newName!=null && !"".equals(newName)) {
            newName = file.getName();
        }
        File file_new = new File(destPath + newName);
        if (file.exists() && file.renameTo(file_new)) {
            flag = true;
        }
        return flag;
    }

    /**
     * 删除该目录下的所有文件
     * @param filePath
     *            文件目录路径
     */
    public static void deleteFiles(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    files[i].delete();
                }
            }
        }
    }

    /**
     * 删除单个文件
     * @param filePath
     *         文件目录路径
     * @param fileName
     *         文件名称
     */
    public static void deleteFile(String filePath, String fileName) {
        File file = new File(filePath + fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 文件排序(根据文件名)
     * @param list_file 文件列表
     * @param order 排序方式(0:不排序;1:升序;2:降序;)
     * @return
     */
    public static List<File> orderByName(List<File> list_file, int order) {
        switch (order) {
            case 1:
                Collections.sort(list_file, new Comparator<File>() {
                    public int compare(File o1, File o2) {
                        if (o1.isDirectory() && o2.isFile())
                            return -1;
                        if (o1.isFile() && o2.isDirectory())
                            return 1;
                        return o1.getName().compareTo(o2.getName());
                    }
                });
                break;
            case 2:
                Collections.sort(list_file, new Comparator<File>() {
                    public int compare(File o1, File o2) {
                        if (o1.isDirectory() && o2.isFile())
                            return -1;
                        if (o1.isFile() && o2.isDirectory())
                            return 1;
                        return o2.getName().compareTo(o1.getName());
                    }
                });
                break;
            default:
                break;
        }
        return list_file;
    }

    public static void main(String[] args) {
        String str = "DEVICE=eth0\n" +
                "HWADDR=00:0C:29:FA:9F:9A\n" +
                "TYPE=Ethernet\n" +
                "UUID=5e6fc777-2a33-44d7-bd72-fb1e11fd11ab\n" +
                "ONBOOT=yes\n" +
                "NM_CONTROLLED=yes\n" +
                "BOOTPROTO=dhcp\n" +
                "IPADDR=192.168.1.135\n" +
                "NETMASK=255.255.255.0\n" +
                "GATEWAY=192.168.1.1";
        String oldStr = "(?<=((^|\\n)IPADDR=))(.+?)(?=($|\\n))";
        String newStr = "IPADDR=192.168.10.2";
        str = str.replaceAll(oldStr, newStr);
        System.out.println(str);

    }

}
