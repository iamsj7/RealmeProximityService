/*
 * Copyright (c) 2020 Harshit Jain <god@hyper-labs.tech>
 * Copyright (c) 2016-2018 The OmniRom Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Purpose: File Handling helper class, Code has been taken from opensource and modifed.
 *
 */
package co.hyper.proximityservice

import java.io.*

object FileHelper {
    private const val TAG = "FileHelper"

    /**
     * Write a string value to the specified file.
     * @param filename      The filename
     * @param value         The value
     */
    @JvmStatic
    fun writeValue(filename: String?, value: String) {
        if (filename == null) {
            return
        }
        try {
            val fos = FileOutputStream(File(filename))
            fos.write(value.toByteArray())
            fos.flush()
            fos.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Check if the specified file exists.
     * @param filename      The filename
     * @return              Whether the file exists or not
     */
    fun fileExists(filename: String?): Boolean {
        return if (filename == null) {
            false
        } else File(filename).exists()
    }

    fun fileWritable(filename: String?): Boolean {
        return fileExists(filename) && File(filename).canWrite()
    }

    fun readLine(filename: String?): String? {
        if (filename == null) {
            return null
        }
        var br: BufferedReader? = null
        var line: String? = null
        try {
            br = BufferedReader(FileReader(filename), 1024)
            line = br.readLine()
        } catch (e: IOException) {
            return null
        } finally {
            if (br != null) {
                try {
                    br.close()
                } catch (e: IOException) {
                    // ignore
                }
            }
        }
        return line
    }

    @JvmStatic
    fun getFileValueAsBoolean(filename: String?, defValue: Boolean): Boolean {
        val fileValue = readLine(filename)
        return if (fileValue != null) {
            if (fileValue == "0") false else true
        } else defValue
    }

    fun getFileValue(filename: String?, defValue: String): String {
        val fileValue = readLine(filename)
        return fileValue ?: defValue
    }
}