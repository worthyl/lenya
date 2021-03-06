/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

/* $Id$  */

package org.apache.lenya.cms.rc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import org.apache.lenya.util.XPSFileOutputStream;
import org.apache.log4j.Logger;

/**
 * Controller for the reserved check-in, check-out, the backup versions and the rollback.
 */
public class RevisionController {
    private static Logger log = Logger.getLogger(RevisionController.class);

    // System username. This is used for 
    // - creating dummy checkin events in a new RCML file
    //   when it is created on-the-fly
    // - system override on checkin, i.e. you can force
    //   a checkin into the repository if you use this
    //   username as identity parameter to reservedCheckIn()
    //
    public static final String systemUsername = "System";

    private String rcmlDir = null;
    private String rootDir = null;
    private String backupDir = null;

    /**
     * Creates a new RevisionController object.
     * 
     */
    public RevisionController() {
        Configuration conf = new Configuration();
        rcmlDir = conf.getRcmlDirectory();
        if (!new File(rcmlDir).exists())
            log.error("No such directory: " + rcmlDir);
        backupDir = conf.getBackupDirectory();
        if (!new File(backupDir).exists())
            log.error("No such directory: " + backupDir);
        rootDir = "conf.rootDirectory";
    }

    /**
     * Creates a new RevisionController object.
     *
     * @param rcmlDirectory The directory for the RCML files
     * @param backupDirectory The directory for the backup versions
     * @param rootDirectory The publication directory
     */
    public RevisionController(String rcmlDirectory, String backupDirectory, String rootDirectory) {
        this.rcmlDir = rcmlDirectory;
        this.backupDir = backupDirectory;
        this.rootDir = rootDirectory;
    }

    /**
     * Creates a new RevisionController object.
     *
     * @param rootDir The publication directory
     */
    public RevisionController(String rootDir) {
        this();
        this.rootDir = rootDir;
    }

    /**
     * Shows Configuration
     *
     * @return String The rcml directory, the backup directory, the publication directory
     */
    public String toString() {
        return "rcmlDir=" + rcmlDir + " , rcbakDir=" + backupDir + " , rootDir=" + rootDir;
    }

    /**
     * Get the RCML File for the file source
     *
     * @param source The path of the file from the publication.
     *
     * @return RCML The corresponding RCML file.
     *
     * @throws FileNotFoundException if an error occurs
     * @throws IOException if an error occurs
     * @throws Exception if an error occurs
     */
/*
    public RCML getRCML(Document doc) throws FileNotFoundException, IOException, Exception {
        return new RCML(rcmlDir, doc.getDocumentAreaPath(), rootDir);
    }
*/

    /**
     * @deprecated please use getRCML(Document doc)
     * Get the RCML File for the file source
     *
     * @param source The path of the file from the publication.
     *
     * @return RCML The corresponding RCML file.
     *
     * @throws FileNotFoundException if an error occurs
     * @throws IOException if an error occurs
     * @throws Exception if an error occurs
     */
    public RCML getRCML(String source) throws FileNotFoundException, IOException, Exception {
        return new RCML(rcmlDir, source, rootDir);
    }

    /**
     * Try to make a reserved check out of the file source for a user with identity
     *
     * @param source The filename of the file to check out
     * @param identity The identity of the user
     * @return File File to check out
     * @throws Exception if an error occurs
     */
/*
    public File reservedCheckOut(Document doc, String identity) throws Exception {

        RCML rcml = new RCML(rcmlDir, doc.getDocumentAreaPath(), rootDir);

        RCMLEntry entry = rcml.getLatestEntry();

        // The same user is allowed to check out repeatedly without
        // having to check back in first.
        //
        if (entry != null) {
            log.debug("entry: " + entry);
            log.debug("entry.type:" + entry.getType());
            log.debug("entry.identity" + entry.getIdentity());
        }

        if ((entry != null)
            && (entry.getType() != RCML.ci)
            && !entry.getIdentity().equals(identity)) {
            throw new FileReservedCheckOutException(rootDir + doc.getDocumentAreaPath(), rcml);
        }

        rcml.checkOutIn(RCML.co, identity, new Date().getTime(), false);

        return doc.getFile();
    }
*/

    /**
     * @deprecated reservedCheckOut(Document, String)
     * Try to make a reserved check out of the file source for a user with identity
     *
     * @param source The filename of the file to check out
     * @param identity The identity of the user
     * @return File File to check out
     * @throws Exception if an error occurs
     */
    public File reservedCheckOut(String source, String identity) throws Exception {
        
        File file = new File(rootDir + source);
        /*
        if (!file.isFile()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        */

        RCML rcml = new RCML(rcmlDir, source, rootDir);

        RCMLEntry entry = rcml.getLatestEntry();

        // The same user is allowed to check out repeatedly without
        // having to check back in first.
        //
        if (entry != null) {
            log.debug("entry: " + entry);
            log.debug("entry.type:" + entry.getType());
            log.debug("entry.identity" + entry.getIdentity());
        }

        if ((entry != null)
            && (entry.getType() != RCML.ci)
            && !entry.getIdentity().equals(identity)) {
            throw new FileReservedCheckOutException(rootDir + source, rcml);
        }

        rcml.checkOutIn(RCML.co, identity, new Date().getTime(), false);

        return file;
    }

    /**
     * Checks if a source can be checked out.
     * @param source The source.
     * @param identity The identity who requests checking out.
     * @return A boolean value.
     * @throws Exception when something went wrong.
     */
/*
    public boolean canCheckOut(Document doc, String identity) throws Exception {
                
        RCML rcml = new RCML(rcmlDir, doc.getDocumentAreaPath(), rootDir);

        RCMLEntry entry = rcml.getLatestEntry();

        // The same user is allowed to check out repeatedly without
        // having to check back in first.
        //
        if (entry != null) {
            log.debug("entry: " + entry);
            log.debug("entry.type:" + entry.getType());
            log.debug("entry.identity" + entry.getIdentity());
        }

        boolean checkedOutByOther =
            entry != null && entry.getType() != RCML.ci && !entry.getIdentity().equals(identity);

        return !checkedOutByOther;
    }
*/

    /**
     * @deprecated please use canCheckOut(Document, String)
     * Checks if a source can be checked out.
     * @param source The source.
     * @param identity The identity who requests checking out.
     * @return A boolean value.
     * @throws Exception when something went wrong.
     */
    public boolean canCheckOut(String source, String identity) throws Exception {
        RCML rcml = new RCML(rcmlDir, source, rootDir);

        RCMLEntry entry = rcml.getLatestEntry();

        // The same user is allowed to check out repeatedly without
        // having to check back in first.
        //
        if (log.isDebugEnabled() && entry != null) {
            log.debug("entry: " + entry);
            log.debug("entry.type:" + entry.getType());
            log.debug("entry.identity" + entry.getIdentity());
        }

        boolean checkedOutByOther =
            entry != null && entry.getType() != RCML.ci && !entry.getIdentity().equals(identity);

        return !checkedOutByOther;
    }

    /**
     * Try to make a reserved check in of the file destination for a user with identity. A backup
     * copy can be made.
     *
     * @param destination The file we want to check in
     * @param identity The identity of the user
     * @param backup if true, a backup will be created, else no backup will be made.
     *
     * @return long The time.
     *
     * @exception FileReservedCheckInException if the document couldn't be checked in (for instance
     *            because it is already checked out by someone other ...)
     * @exception Exception if other problems occur
     */
/*
    public long reservedCheckIn(Document doc, String identity, boolean backup)
        throws FileReservedCheckInException, Exception {
        
        RCML rcml = new RCML(rcmlDir, doc.getDocumentAreaPath(), rootDir);

        CheckOutEntry coe = rcml.getLatestCheckOutEntry();
        CheckInEntry cie = rcml.getLatestCheckInEntry();

        // If there has never been a checkout for this object
        // *or* if the user attempting the checkin right now
        // is the system itself, we will skip any checks and proceed
        // right away to the actual checkin.
        // In all other cases we enforce the revision control
        // rules inside this if clause:
        //
        if (!((coe == null) || identity.equals(RevisionController.systemUsername))) {
            if ((cie != null) && (cie.getTime() > coe.getTime())) {
                // We have case 1
                if (!cie.getIdentity().equals(identity)) {
                    // Case 1.2., abort...
                    //
                    throw new FileReservedCheckInException(rootDir + doc.getDocumentAreaPath(), rcml);
                }
            } else {
                // Case 2
                if (!coe.getIdentity().equals(identity)) {
                    // Case 2.2., abort...
                    //
                    throw new FileReservedCheckInException(rootDir + doc.getDocumentAreaPath(), rcml);
                }
            }
        }

        File originalFile = doc.getFile();
        long time = new Date().getTime();

        if (backup && originalFile.isFile()) {
            File backupFile = new File(backupDir, doc.getDocumentAreaPath() + ".bak." + time);
            File parent = new File(backupFile.getParent());

            if (!parent.isDirectory()) {
                parent.mkdirs();
            }

            log.info(
                "Backup: copy "
                    + originalFile.getAbsolutePath()
                    + " to "
                    + backupFile.getAbsolutePath());

            InputStream in = new FileInputStream(originalFile.getAbsolutePath());

            OutputStream out = new XPSFileOutputStream(backupFile.getAbsolutePath());
            byte[] buffer = new byte[512];
            int length;

            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }

            out.close();
        }

        rcml.checkOutIn(RCML.ci, identity, time, backup);
        rcml.pruneEntries(backupDir);
        rcml.write();

        // FIXME: If we reuse the observer pattern as implemented in
        // xps this would be the place to notify the observers,
        // e.g. like so:
        // 	StatusChangeSignalHandler.emitSignal("file:" + originalFile.getAbsolutePath(),
        // 					     "reservedCheckIn");
        return time;
    }
*/

    /**
     * @deprecated please use reservedCheckIn(Document, String, boolean)
     * Try to make a reserved check in of the file destination for a user with identity. A backup
     * copy can be made.
     *
     * @param destination The file we want to check in
     * @param identity The identity of the user
     * @param backup if true, a backup will be created, else no backup will be made.
     *
     * @return long The time.
     *
     * @exception FileReservedCheckInException if the document couldn't be checked in (for instance
     *            because it is already checked out by someone other ...)
     * @exception Exception if other problems occur
     */
    public long reservedCheckIn(String destination, String identity, boolean backup)
        throws FileReservedCheckInException, Exception {
        RCML rcml = new RCML(rcmlDir, destination, rootDir);

        CheckOutEntry coe = rcml.getLatestCheckOutEntry();
        CheckInEntry cie = rcml.getLatestCheckInEntry();

        // If there has never been a checkout for this object
        // *or* if the user attempting the checkin right now
        // is the system itself, we will skip any checks and proceed
        // right away to the actual checkin.
        // In all other cases we enforce the revision control
        // rules inside this if clause:
        //
        if (!((coe == null) || identity.equals(RevisionController.systemUsername))) {
            /*
             * Possible cases and rules:
             *
             * 1.) we were able to read the latest checkin and it is later than latest checkout
             *     (i.e. there is no open checkout to match this checkin, an unusual case)
             *     1.1.) identity of latest checkin is equal to current user
             *           -> checkin allowed, same user may check in repeatedly
             *     1.2.) identity of latest checkin is not equal to current user
             *           -> checkin rejected, may not overwrite the revision which
             *              another user checked in previously
             * 2.) there was no checkin or the latest checkout is later than latest checkin
             *     (i.e. there is an open checkout)
             *     2.1.) identity of latest checkout is equal to current user
             *           -> checkin allowed, user checked out and may check in again
             *              (the most common case)
             *     2.2.) identity of latest checkout is not equal to current user
             *           -> checkin rejected, may not check in while another
             *              user is working on this document
             *
             */
            if ((cie != null) && (cie.getTime() > coe.getTime())) {
                // We have case 1
                if (!cie.getIdentity().equals(identity)) {
                    // Case 1.2., abort...
                    //
                    throw new FileReservedCheckInException(rootDir + destination, rcml);
                }
            } else {
                // Case 2
                if (!coe.getIdentity().equals(identity)) {
                    // Case 2.2., abort...
                    //
                    throw new FileReservedCheckInException(rootDir + destination, rcml);
                }
            }
        }

        File originalFile = new File(rootDir, destination);
        long time = new Date().getTime();

        if (backup && originalFile.isFile()) {
            File backupFile = new File(backupDir, destination + ".bak." + time);
            File parent = new File(backupFile.getParent());

            if (!parent.isDirectory()) {
                parent.mkdirs();
            }

            log.info(
                "Backup: copy "
                    + originalFile.getAbsolutePath()
                    + " to "
                    + backupFile.getAbsolutePath());

            InputStream in = new FileInputStream(originalFile.getAbsolutePath());

            OutputStream out = new XPSFileOutputStream(backupFile.getAbsolutePath());
            byte[] buffer = new byte[512];
            int length;

            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }

            out.close();
        }

        rcml.checkOutIn(RCML.ci, identity, time, backup);
        rcml.pruneEntries(backupDir);
        rcml.write();

        // FIXME: If we reuse the observer pattern as implemented in
        // xps this would be the place to notify the observers,
        // e.g. like so:
        // 	StatusChangeSignalHandler.emitSignal("file:" + originalFile.getAbsolutePath(),
        // 					     "reservedCheckIn");
        return time;
    }

    /**
     * Get the absolute path of a backup version  
     *
     * @param time The time of the backup 
     * @param filename The path of the file from the {publication}
     *
     * @return String The absolute path of the backup version
     */
    public String getBackupFilename(long time, String filename) {
        File backup = new File(backupDir, filename + ".bak." + time);

        return backup.getAbsolutePath();
    }

    /**
     * Get the file of a backup version  
     *
     * @param time The time of the backup 
     * @param filename The path of the file from the {publication}
     *
     * @return File The file of the backup version
     */
/*
    public File getBackupFile(long time, Document doc) {        
        File backup = new File(backupDir, doc.getDocumentAreaPath() + ".bak." + time);
        return backup;
    }
*/

    /**
     * @deprecated getBackupFile(long, Document)
     * Get the file of a backup version  
     *
     * @param time The time of the backup 
     * @param filename The path of the file from the {publication}
     *
     * @return File The file of the backup version
     */
    public File getBackupFile(long time, String filename) {
        File backup = new File(backupDir, filename + ".bak." + time);

        return backup;
    }

    /**
     * Rolls back to the given point in time.  
     *
     * @param destination File which will be rolled back
     * @param identity The identity of the user
     * @param backupFlag If true, a backup of the current version will be made before the rollback
     * @param time The time point of the desired version
     *
     * @return long The time of the version to roll back to.
     *
     * @exception FileReservedCheckInException if the current version couldn't be checked in again
     * @exception FileReservedCheckOutException if the current version couldn't be checked out
     * @exception FileNotFoundException if a file couldn't be found
     * @exception Exception if another problem occurs
     */
/*
    public long rollback(Document doc, String identity, boolean backupFlag, long time)
        throws
            FileReservedCheckInException,
            FileReservedCheckOutException,
            FileNotFoundException,
            Exception {
        
        // Make sure the old version exists
        //
        File backup = new File(backupDir, doc.getDocumentAreaPath() + ".bak." + time);
        File current = doc.getFile();

        if (!backup.isFile()) {
            throw new FileNotFoundException(backup.getAbsolutePath());
        }

        if (!current.isFile()) {
            throw new FileNotFoundException(current.getAbsolutePath());
        }

        // Try to check out current version
        //
        reservedCheckOut(doc, identity);

        // Now roll back to the old state
        //
        FileInputStream in = new FileInputStream(backup.getAbsolutePath());

        XPSFileOutputStream out = new XPSFileOutputStream(current.getAbsolutePath());
        byte[] buffer = new byte[512];
        int length;

        while ((length = in.read(buffer)) != -1) {
            out.write(buffer, 0, length);
        }

        out.close();

        // Try to check back in, this might cause
        // a backup of the current version to be created if
        // desired by the user.
        //
        long newtime = reservedCheckIn(doc, identity, backupFlag);

        return newtime;
    }
*/

    /**
     * @deprecated please use rollback(Document, String, boolean, long)
     * Rolls back to the given point in time.  
     *
     * @param destination File which will be rolled back
     * @param identity The identity of the user
     * @param backupFlag If true, a backup of the current version will be made before the rollback
     * @param time The time point of the desired version
     *
     * @return long The time of the version to roll back to.
     *
     * @exception FileReservedCheckInException if the current version couldn't be checked in again
     * @exception FileReservedCheckOutException if the current version couldn't be checked out
     * @exception FileNotFoundException if a file couldn't be found
     * @exception Exception if another problem occurs
     */
    public long rollback(String destination, String identity, boolean backupFlag, long time)
        throws
            FileReservedCheckInException,
            FileReservedCheckOutException,
            FileNotFoundException,
            Exception {
        // Make sure the old version exists
        //
        File backup = new File(backupDir, destination + ".bak." + time);
        File current = new File(rootDir, destination);

        if (!backup.isFile()) {
            throw new FileNotFoundException(backup.getAbsolutePath());
        }

        if (!current.isFile()) {
            throw new FileNotFoundException(current.getAbsolutePath());
        }

        // Try to check out current version
        //
        reservedCheckOut(destination, identity);

        // Now roll back to the old state
        //
        FileInputStream in = new FileInputStream(backup.getAbsolutePath());

        XPSFileOutputStream out = new XPSFileOutputStream(current.getAbsolutePath());
        byte[] buffer = new byte[512];
        int length;

        while ((length = in.read(buffer)) != -1) {
            out.write(buffer, 0, length);
        }

        out.close();

        // Try to check back in, this might cause
        // a backup of the current version to be created if
        // desired by the user.
        //
        long newtime = reservedCheckIn(destination, identity, backupFlag);

        return newtime;
    }

    /**
     * Delete the check in and roll back the file to the backup at time
     *
     * @param time The time point of the back version we want to retrieve
     * @param destination The File for which we want undo the check in
     *
     * @exception Exception FileNotFoundException if the back  version or the current version
     *            couldn't be found
     */
    public void undoCheckIn(long time, String destination) throws Exception {
        File backup = new File(backupDir + "/" + destination + ".bak." + time);
        File current = new File(rootDir + destination);

        RCML rcml = new RCML(rcmlDir, destination, rootDir);

        if (!backup.isFile()) {
            throw new FileNotFoundException(backup.getAbsolutePath());
        }

        if (!current.isFile()) {
            throw new FileNotFoundException(current.getAbsolutePath());
        }

        FileInputStream in = new FileInputStream(backup.getAbsolutePath());

        XPSFileOutputStream out = new XPSFileOutputStream(current.getAbsolutePath());
        byte[] buffer = new byte[512];
        int length;

        while ((length = in.read(buffer)) != -1) {
            out.write(buffer, 0, length);
        }

        log.info("Undo: copy " + backup.getAbsolutePath() + " " + current.getAbsolutePath());

        rcml.deleteFirstCheckIn();
        out.close();
    }

    /**
     * delete the revisions
     * @param filename of the document
     * @throws RevisionControlException when somthing went wrong
     */
/*
    public void deleteRevisions(Document doc) throws RevisionControlException{
        try {
            RCML rcml = this.getRCML(doc);
            String[] times = rcml.getBackupsTime();
            for (int i=0; i < times.length; i++) {
                long time = Long.parseLong(times[i]);
                File backup = this.getBackupFile(time, doc);
                File parentDirectory = null; 
                parentDirectory = backup.getParentFile(); 
                boolean deleted = backup.delete();
                if (!deleted) {
                    throw new RevisionControlException("The backup file, "+backup.getCanonicalPath()+" could not be deleted!");
                }
                if (parentDirectory != null 
                    && parentDirectory.exists()
                    && parentDirectory.isDirectory()
                    && parentDirectory.listFiles().length == 0) {
                        parentDirectory.delete();
                }
            }
		} catch (Exception e) {
            throw new RevisionControlException(e);
		}
    }
*/

    /**
     * @deprecated please use deleteRevisions(Document)
     * delete the revisions
	 * @param filename of the document
	 * @throws RevisionControlException when somthing went wrong
	 */
	public void deleteRevisions(String filename) throws RevisionControlException{
        try {
			RCML rcml = this.getRCML(filename);
            String[] times = rcml.getBackupsTime();
            for (int i=0; i < times.length; i++) {
                long time = Long.parseLong(times[i]);
                File backup = this.getBackupFile(time, filename);
                File parentDirectory = null; 
                parentDirectory = backup.getParentFile(); 
                boolean deleted = backup.delete();
                if (!deleted) {
                    throw new RevisionControlException("The backup file, "+backup.getCanonicalPath()+" could not be deleted!");
                }
                if (parentDirectory != null 
                    && parentDirectory.exists()
                    && parentDirectory.isDirectory()
                    && parentDirectory.listFiles().length == 0) {
                        parentDirectory.delete();
                }
            }
		} catch (Exception e) {
            throw new RevisionControlException(e);
		}
    }
    
    /**
     * delete the rcml file
     * @param filename of the document
     * @throws RevisionControlException if something went wrong
     */
/*
    public void deleteRCML(Document doc) throws RevisionControlException{
        try {
            RCML rcml = this.getRCML(doc);
            boolean deleted = rcml.delete();
            if (!deleted) {
                throw new RevisionControlException("The rcml file could not be deleted!");
            }
        } catch (Exception e) {
            throw new RevisionControlException(e);
        }
    }
*/
    
    /**
     * @deprecated please use deleteRCML(Document)
     * delete the rcml file
	 * @param filename of the document
	 * @throws RevisionControlException if something went wrong
	 */
	public void deleteRCML(String filename) throws RevisionControlException{
        try {
            RCML rcml = this.getRCML(filename);
            boolean deleted = rcml.delete();
            if (!deleted) {
                throw new RevisionControlException("The rcml file could not be deleted!");
            }
        } catch (Exception e) {
            throw new RevisionControlException(e);
        }
    }
    
}
