package dev.silvadev.blockode.utils;

import android.content.Context;
import android.content.res.AssetManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

  FileUtil() {}

  public static boolean isExistFile(String path) {
    return new File(path).exists();
  }

  public static void makeDir(String path) {
    if (!isExistFile(path)) {
      new File(path).mkdirs();
    }
  }
  
  public static void makeDir(File file) {
    if (!file.exists()) {
      new File(file.getAbsolutePath()).mkdirs();
    }
  }

  public static void createNewFileIfNotPresent(String path) {
    int lastSep = path.lastIndexOf(File.separator);
    if (lastSep > 0) {
      String dirPath = path.substring(0, lastSep);
      makeDir(dirPath);
    }

    File file = new File(path);

    try {
      if (!file.exists()) file.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static String readFile(String path, boolean createIfNotExists) {
    if (createIfNotExists) createNewFileIfNotPresent(path);
    StringBuilder sb = new StringBuilder();
    try (FileReader fr = new FileReader(path)) {
      char[] buff = new char[1024];
      int length;

      while ((length = fr.read(buff)) > 0) {
        sb.append(new String(buff, 0, length));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return sb.toString();
  }

  public static String readFileIfExist(String path) {
    StringBuilder sb = new StringBuilder();
    try (FileReader fr = new FileReader(path)) {
      char[] buff = new char[1024];
      int length;

      while ((length = fr.read(buff)) > 0) {
        sb.append(new String(buff, 0, length));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return sb.toString();
  }

  public static void writeText(final String path, final String text) {
    createNewFileIfNotPresent(path);

    try (FileWriter fileWriter = new FileWriter(path, false)) {
      fileWriter.write(text);
      fileWriter.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static void deleteFile(String path) {
		File file = new File(path);
		
		if (!file.exists()) return;
		
		if (file.isFile()) {
			file.delete();
			return;
		}
		
		File[] fileArr = file.listFiles();
		
		if (fileArr != null) {
			for (File subFile : fileArr) {
				if (subFile.isDirectory()) {
					deleteFile(subFile.getAbsolutePath());
				}
				
				if (subFile.isFile()) {
					subFile.delete();
				}
			}
		}
		
		file.delete();
	}
  
  public static File getPackageDataDir(Context context) {
  	return context.getExternalFilesDir(null);
  }
  
  public static File getClassPathDir(Context context) {
  	return new File(getPackageDataDir(context), "classpath/");
  }
  
  public static File getIndexFile(Context context) {
  	return new File(getPackageDataDir(context), "index.json");
  }
  
  public static List<File> listOf(File file) {
      File[] fileArray = file.listFiles();
      List<File> files = new ArrayList<>();
      for(File filee : fileArray) {
      	files.add(filee);
      }
      return files.size() > 0 ? files : new ArrayList<>();
  }
  
  public static String getFileExtension(String name) {
        String[] parts = name.split("\\.");
        return parts.length > 1 ? parts[parts.length - 1] : "";
  }
  
  public static String readAsset(AssetManager assets, String path) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(assets.open(path)));
        StringBuilder builder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            builder.append(line).append("\n");
        }

        reader.close();
        return builder.toString();
  }
  
  public static void extractAssetFile(Context context,String file,String to) throws Exception {
		if(!isExistFile(to)) writeText(to,"");
		int count;
		java.io.InputStream input= context.getAssets().open(file);
		java.io.OutputStream output = new  java.io.FileOutputStream(to);
		byte data[] = new byte[1024];
		while ((count = input.read(data))>0) {
			output.write(data, 0, count);
		}
		output.flush();
		output.close();
		input.close();
  }
}
