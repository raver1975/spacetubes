/*
   Copyright 2012 Korphane Studio
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.klemstinegroup.spacetubes;

/**
 * A very simple PID controller.  Based on code from Flight of the Abductor.
 * 
 * @author Chris Livingston
 *
 */
public class SimplePID 
{
	private float [] history;
	
	private float kI;
	private float kD;
	private float kP;
	private float output;
	private float offset;
	
	private float max, min;
	
	private float controllerTime;
	private float samplePeriod;

	private float vI;
	
	/**
	 * Sets up the controller
	 * @param kP Proportional constant
	 * @param kI Integral (historical) constant
	 * @param kD Derivative (change rate) constant
	 * @param integralSize Number of frames to remember
	 */
	public SimplePID(float kP, float kI, float kD, int integralSize, float samplePeriod)
	{
		this.kP = kP;
		this.kI = kI;
		this.kD = kD;
				
		this.samplePeriod = samplePeriod;
		history = new float[integralSize];
	}
	
	/**
	 * Sets output clamping variables
	 * @param max
	 * @param min
	 */
	public void setClamping(float max, float min)
	{
		this.max = max;
		this.min = min;			
	}
	
	/**
	 * Sets an output offset
	 * @param offset
	 */
	public void setOffset(float offset)
	{
		this.offset = offset;
	}
		
	/**
	 * Updates the PID controller
	 * @param in Sensor input
	 * @param set Set point
	 * @param deltaTime Time since last reading
	 */
	public void update(float in, float set, float deltaTime)
	{
		float vP = set - in;//value of proportional portion
	
		//Update the integrator
		controllerTime += deltaTime;
		if(controllerTime > samplePeriod)
		{
			controllerTime -= samplePeriod;
			vI = 0f; //Integration area
			
			//Push the error on the stack
			for(int i = history.length - 1; i > 0; i--)
			{
				vI += history[i];
				history[i] = history[i-1];
			}
			
			history[0] = vP; //Push the error (proportional bit) to the stack
			
		}
		
		float vD = (history[0] - history[1]) * deltaTime; //Derivative value
		
		output = vI*kI+vD*kD+vP*kP+offset;
		
		if(max > min)
		{
			output = Math.max(output, min);
			output = Math.min(output, max);
		}
	}
	
	/**
	 * Retrieves the PID output
	 * @return
	 */
	public float getOutput()
	{
		return output;
	}

}