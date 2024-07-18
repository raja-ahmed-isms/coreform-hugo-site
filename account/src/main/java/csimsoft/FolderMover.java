/**
 * @(#)FolderMover.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2012/3/12
 */

package csimsoft;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;


public class FolderMover
{
   public static class FolderData
   {
      public File Source = null;
      public File Target = null;

      public FolderData()
      {
      }

      public FolderData(File source, File target)
      {
         this.Source = source;
         this.Target = target;
      }
   }

   private ArrayList<File> NewFolders = new ArrayList<File>();
   private ArrayList<File> OldFolders = new ArrayList<File>();
   private TreeMap<File, File> MovedFiles = new TreeMap<File, File>();

   public FolderMover()
   {
   }

   public void addNewFolder(File folder)
   {
      this.NewFolders.add(0, folder);
   }

   public boolean moveFolder(ResultMessageAdapter message, File source, File target)
   {
      boolean success = true;
      ArrayList<FolderData> queue = new ArrayList<FolderData>();
      queue.add(new FolderMover.FolderData(source, target));
      while(!queue.isEmpty())
      {
         // Create a new folder.
         FolderMover.FolderData data = queue.remove(0);
         success = data.Target.mkdirs();
         if(!success)
         {
            message.setResult(ResultMessage.Failure);
            message.addMessage("<br />Unable to create a new folder.");
            break;
         }

         this.NewFolders.add(0, data.Target);

         // Move all the files to the new folder. Add sub-folders to
         // the queue.
         File[] files = data.Source.listFiles();
         for(int i = 0; success && files != null && i < files.length; i++)
         {
            source = files[i];
            target = new File(data.Target, source.getName());
            if(source.isDirectory())
               queue.add(new FolderMover.FolderData(source, target));
            else
            {
               success = source.renameTo(target);
               if(success)
                  this.MovedFiles.put(source, target);
            }
         }

         if(!success)
         {
            message.setResult(ResultMessage.Failure);
            message.addMessage("<br />Unable to move a file.");
            break;
         }

         this.OldFolders.add(0, data.Source);
      }

      return success;
   }

   public void revertMove(ResultMessageAdapter message)
   {
      // Move the files back to the old folder.
      int failed = 0;
      Iterator<File> iter = this.MovedFiles.navigableKeySet().iterator();
      while(iter.hasNext())
      {
         File original = iter.next();
         if(!this.MovedFiles.get(original).renameTo(original))
            ++failed;
      }

      if(failed == 1)
         message.addMessage("<br />Unable to move back one file.");
      else if(failed > 1)
         message.addMessage("<br />Unable to move back " + failed + " files.");

      // Remove the new folder(s) from the disk on failure.
      iter = this.NewFolders.iterator();
      while(iter.hasNext())
         iter.next().delete();

      this.clear();
   }

   public void completeMove(ResultMessageAdapter message)
   {
      // Clean up the old folder(s).
      int failed = 0;
      Iterator<File> iter = this.OldFolders.iterator();
      while(iter.hasNext())
      {
         if(!iter.next().delete())
            ++failed;
      }

      if(failed == 1)
         message.addMessage("<br />Unable to remove one old folder.");
      else if(failed > 1)
         message.addMessage("<br />Unable to remove " + failed + " old folders.");

      this.clear();
   }

   private void clear()
   {
      this.NewFolders.clear();
      this.OldFolders.clear();
      this.MovedFiles.clear();
   }
}