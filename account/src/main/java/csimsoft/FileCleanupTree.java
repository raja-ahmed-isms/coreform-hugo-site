/**
 * @(#)FileCleanupTree.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2012/6/8
 */

package csimsoft;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;


public class FileCleanupTree
{
   private TreeMap<String, File> Items = new TreeMap<String, File>();

   public FileCleanupTree()
   {
   }

   public int getNumberOfFiles()
   {
      return this.Items.size();
   }

   public void buildFileTree(String rootPath)
   {
      this.Items.clear();

      File root = new File(rootPath);
      if(!root.exists() || !root.isDirectory())
         return;

      ArrayList<File> files = new ArrayList<File>();
      files.add(root);
      while(!files.isEmpty())
      {
         File file = files.remove(files.size() - 1);
         String[] list = file.list();
         for(int i = 0; list != null && i < list.length; ++i)
         {
            File child = new File(file, list[i]);
            String path = child.getAbsolutePath();
            this.Items.put(path, child);
            if(child.isDirectory())
               files.add(child);
         }
      }
   }

   public boolean matchPath(String path)
   {
      File file = this.Items.remove(path);
      return file != null;
   }

   public int removeFiles()
   {
      int remaining = 0;
      Iterator<File> iter = this.Items.descendingMap().values().iterator();
      while(iter.hasNext())
      {
         File file = iter.next();
         if(!file.delete())
            ++remaining;
      }

      return remaining;
   }
}
