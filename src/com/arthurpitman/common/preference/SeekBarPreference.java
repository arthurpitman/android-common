/*
 * Copyright (C) 2012, 2013 Arthur Pitman
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
 */

package com.arthurpitman.common.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.arthurpitman.common.R;


/**
 * A preference that displays a seek bar in a dialog.
 * <p>
 * The seek bar itself supports a value between 0 and 100.
 * However this be mapped to any double and / or displayed as a String
 * via the {@link Mapper}.
 */
public class SeekBarPreference extends DialogPreference {

	/**
	 * Minimum seek bar value.
	 */
	public static final int SEEK_BAR_MIN_VALUE = 0;
	
	/**
	 * Maximum seek bar value.
	 */
	public static final int SEEK_BAR_MAX_VALUE = 100;
	
	
	/**
	 * Maps integer seek bar value to a double and / or {@link String}.	 
	 */
	public interface Mapper {
		double mapValueToDouble(int value);
		String mapValueToString(int value);
	}
	
	
	/**
	 * The associated {@link Mapper}.
	 */
	private Mapper mapper; 	

	/**
	 * The message {@link TextView}.
	 */
	private TextView messageTextView;
	
	/**
	 * Current value of the seek bar.
	 */
	private int seekBarValue;
	
	/**
	 * Current value of the {@code SeekBarPreference}.
	 */
	private int value;
	
	
	/**
	 * Creates a new {@code SeekBarPreference}.
	 * @param context
	 * @param attrs
	 */
    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializePreference(context, attrs);
    }

  
    /**
     * Creates a new {@code SeekBarPreference}.
     * @param context
     * @param attrs
     * @param defStyle
     */
    public SeekBarPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializePreference(context, attrs);
    }

    
	/**
	 * Performs shared initialization.
	 */
    private void initializePreference(Context context, AttributeSet attrs) {
    	setDialogLayoutResource(R.layout.seek_bar_preference);
    }      
    
    
    /**
     * Gets the preference value.
     * @return
     */
    public int getValue() {
        return value;
    }

    
    /**
     * Sets the preference value.
     * @param value
     */
	public void setValue(int value) {		
		// ensure value is within the valid range 
		value = Math.max(SEEK_BAR_MIN_VALUE, Math.min(value, SEEK_BAR_MAX_VALUE));
	   
	    if (this.value != value) {	    	   
	    	this.value = value;
	   	 	if (shouldPersist()) {
	 	    	persistInt(value);
	 	    }
	    	notifyChanged();	   
	    }
	}
	
	
	/**
	 * Gets the associated {@link Mapper}.
	 * @return
	 */
	public Mapper getMapper() {
		return mapper;
	}

	
	/**
	 * Sets the associated {@link Mapper}.
	 * @param mapper
	 */
	public void setMapper(Mapper mapper) {
		this.mapper = mapper;
	}
	
	
	@Override
	protected View onCreateDialogView() {
		View view = super.onCreateDialogView();    
		messageTextView = (TextView)view.findViewById(R.id.preference_message_text_view);
		
		// display a message only if a mapper is available
		if (mapper == null) {
			messageTextView.setVisibility(View.GONE);
		} else {
			messageTextView.setText(mapper.mapValueToString(value));             
		}
		
		// set up the seek bar
	    SeekBar seekbar = (SeekBar) view.findViewById(R.id.preference_seekbar);    
	    seekbar.setMax(SEEK_BAR_MAX_VALUE);
	    seekbar.setProgress(value);
	    seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
	
	        @Override
	        public void onStopTrackingTouch(SeekBar seekBar) { }
	
	        @Override
	        public void onStartTrackingTouch(SeekBar seekBar) { }
	
	        @Override
	        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		        if (fromUser) {		        	
		        	// record value in the preference and map it to a string 
	                SeekBarPreference.this.seekBarValue = progress;
	                if (mapper != null) {
	                	messageTextView.setText(mapper.mapValueToString(progress));
	                }
		        }
	        }
	        
	    });
	    return view;
	}
	
	@Override
	protected void onDialogClosed(boolean positiveResult) {       
	    if (positiveResult && callChangeListener(seekBarValue)) {
	    	setValue(seekBarValue);
	    }
	    super.onDialogClosed(positiveResult);
	}
	
	@Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
    	return a.getInt(index, 0);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
    	setValue(restoreValue ? getPersistedInt(value) : (Integer) defaultValue);
    }

    
    @Override
    public CharSequence getSummary() {
        if (mapper != null) {
        	return mapper.mapValueToString(value);
        } else {
        	return super.getSummary();
        }
    }
}