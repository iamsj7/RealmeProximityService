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
 * Purpose: This class when called upon by the Display helper class registers the
 * proximity sensor and listener based upon some sanity checks and updates the value
 * of proximity mask node after 150ms of a successful far event from stk_st2x2x 
 * Infrared proximity sensor used on realme mobiles.
 * 
 */
package co.hyper.proximityservice

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.util.Log
import co.hyper.proximityservice.FileHelper.getFileValueAsBoolean
import co.hyper.proximityservice.FileHelper.writeValue

class InfraredSensor(context: Context) : SensorEventListener {
    private val mContext: Context
    private val mSensorManager: SensorManager
    private val mSensor: Sensor
    override fun onSensorChanged(event: SensorEvent) {
        /* if we are here this means sensor live and is being used */
        sensorAlive = true
        if (event.values[0] == 0.0f) {
            /* We don't need to do anything since the sensor is near */
            if (DEBUG) Log.d(TAG, "Exiting since near the sensor")
            return
        }
        /* Let's do stuff ? */if (DEBUG) Log.d(TAG, "Sending proximity far event in 150ms")
        Handler().postDelayed({ sendFar() }, 150)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        /* Empty */
    }

    fun enable() {
        if (getFileValueAsBoolean(PS_STATUS, false)) {
            if (DEBUG) Log.d(TAG, "Enabling QTI Proximity Sensor fd_enable was 1")
            sensorAlive = true
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            if (DEBUG) Log.d(TAG, "Not a touchpanel proximity event")
            return
        }
    }

    fun disable() {
        if (sensorAlive == true) {
            if (DEBUG) Log.d(TAG, "Disabling QTI Proximity")
            sensorAlive = false
            mSensorManager.unregisterListener(this, mSensor)
        } else {
            if (DEBUG) Log.d(TAG, "Sensor wasn't registered no need of killing")
            return
        }
    }

    /* Set proximity status as far */
    fun sendFar() {
        if (DEBUG) Log.d(TAG, "Sent far event to Proximity mask node")
        writeValue(PS_MASK, "1")
    }

    companion object {
        private const val DEBUG = true
        private const val TAG = "InfraredSensor"
        private const val SENSORID = 33171005 //stk_st2x2x
        private const val PS_STATUS = "/proc/touchpanel/fd_enable"
        private const val PS_MASK = "/proc/touchpanel/prox_mask"

        // Store last status
        private var sensorAlive = false
    }

    init {
        if (DEBUG) Log.d(TAG, "Intialising InfraDED sensor constructor")
        mContext = context
        mSensorManager = mContext.getSystemService(SensorManager::class.java)
        mSensor = mSensorManager.getDefaultSensor(SENSORID, true)
    }
}