package com.googlecode.loveemu.PetiteMM;

public class MMLEvent {

	/**
	 * MML command text.
	 */
	private String command;

	/**
	 * MML command parameters.
	 */
	private String[] params;

	/**
	 * Construct a new MML event.
	 */
	public MMLEvent()
	{
		this(null, null);
	}

	/**
	 * Construct a new MML event.
	 * @param command MML command text.
	 */
	public MMLEvent(String command) {
		this(command, null);
	}

	/**
	 * Construct a new MML event.
	 * @param command MML command text.
	 * @param params MML command parameters.
	 */
	public MMLEvent(String command, String[] params) {
		this.command = command;
		this.params = params;
	}

	/**
	 * Get command text.
	 * @return MML command text without parameters.
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Set command text.
	 * @param command MML commend text.
	 */
	public void setCommand(String command) {
		this.command = command;
	}

	/**
	 * Get MML command parameter texts.
	 * @return Array of MML command parameters.
	 */
	public String[] getParams() {
		return params;
	}

	/**
	 * Set MML command parameter texts.
	 * @param params Array of MML command parameters.
	 */
	public void setParams(String[] params) {
		this.params = params;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		if (command != null) {
			buf.append(command);
		}
		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				if (i != 0)
					buf.append(",");
				buf.append(params[i]);
			}
		}
		return buf.toString();
	}
}
