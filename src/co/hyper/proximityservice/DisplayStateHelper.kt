/*
 * Copyright (c) 2020 Harshit Jain <god@hyper-labs.tech>
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
 * Purpose: This file registers display state listener and makes sure proximity
 * sensor is only triggered on display.STATE_OFF and when fd_enable = 1 which
 * signifies that the system has held a proximity wakelock indicating
 * touchpanel proximity has suspended and no events are recieved from it thus
 * its now time to trigger the infrared proximity and take events from it.
 * 
 */
package co.hyper.proximityservice

import android.content.Context
import android.hardware.display.DisplayManager
import android.hardware.display.DisplayManager.DisplayListener
import android.util.Log
import android.view.Display

class DisplayStateHelper(context: Context) : DisplayListener {
    private val mcontext: Context
    private val mFakeProximity: InfraredSensor
    override fun onDisplayAdded(displayId: Int) {
        /* Empty */
    }

    override fun onDisplayRemoved(displayId: Int) {
        /* Empty "*/
    }

    override fun onDisplayChanged(displayId: Int) {
        if (displayId == Display.DEFAULT_DISPLAY && isDefaultDisplayOff(mcontext)) {
            // register proximity
            if (DEBUG) Log.d(TAG, "Display OFF, Attempting to register proximity sensor")
            mFakeProximity.enable()
        } else {
            // unregister proximity
            if (DEBUG) Log.d(TAG, "Display ON, Attempting to unregister porximity sensor")
            mFakeProximity.disable()
        }
    }

    fun enable() {
        /* Enable the lister */
        if (DEBUG) Log.d(TAG, "Registering display state listner")
        mcontext.getSystemService(DisplayManager::class.java).registerDisplayListener(this, null)
        mFakeProximity.disable()
    }

    fun disable() {
        /* kill the listener */
        if (DEBUG) Log.d(TAG, "Killing display state listener")
        mcontext.getSystemService(DisplayManager::class.java).unregisterDisplayListener(this)
        mFakeProximity.disable()
    }

    companion object {
        private const val TAG = "DisplayStateListener"
        private const val DEBUG = true
        private fun isDefaultDisplayOff(context: Context): Boolean {
            val display = context.getSystemService(DisplayManager::class.java).getDisplay(Display.DEFAULT_DISPLAY)
            return display.state == Display.STATE_OFF
        }
    }

    init {
        if (DEBUG) Log.d(TAG, "Initialising display state listner constructor")
        mcontext = context
        mFakeProximity = InfraredSensor(context)
    }
}