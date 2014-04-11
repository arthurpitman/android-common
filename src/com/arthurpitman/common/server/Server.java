/*
 * Copyright (C) 2012 - 2014 Arthur Pitman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.arthurpitman.common.server;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


/**
 * Server for executing Commands.
 */
public class Server {

	/**
	 * Internal Handler for executing Tasks on a worker thread.
	 */
	private static class ServerHandler extends Handler {
		private static final String TAG = "ServerHandler";
		private WeakReference<Server> outer;


		/**
		 * Creates a new ServerHandler.
		 * @param looper
		 * @param server
		 */
		public ServerHandler(Looper looper, Server server) {
			super(looper);
			outer = new WeakReference<Server>(server);
		}

		@Override
		public void handleMessage(Message message) {
			Server server = outer.get();
			Task task  = (Task) message.obj;
			try {
				task.setSuccess(task.run(server.getContext()));
			} catch (Exception e) {
				Log.d(TAG, "exception while executing task: " + e.toString());
				e.printStackTrace();
				task.setSuccess(false);
			}

			if (task.getCallback() != null) {
				Handler callbackHandler = server.callbackHandler;
				callbackHandler.sendMessage(Message.obtain(callbackHandler, 0, task));
			}
		}
	}


	/**
	 * Internal Handler for dispatching callbacks.
	 */
	private static class CallbackHandler extends Handler {
		private static final String TAG = "CallbackHandler";

		@Override
		public void handleMessage(Message message) {
			Task task  = (Task) message.obj;
			try {
				// force memory synchronization by reading success flag
				task.getCallback().run(task, task.isSuccess());
			} catch (Exception e) {
				Log.d(TAG, "exception while executing task callback: " + e.toString());
				e.printStackTrace();
			}
		}
	}


	private ServerHandler serverHandler;
	private CallbackHandler callbackHandler;
	private final SharedContext context;


	/**
	 * Creates a new Server.
	 * @param serverThread
	 * @param context
	 */
	public Server(HandlerThread serverThread, SharedContext context) {
		this.context = context;
		serverHandler = new ServerHandler(serverThread.getLooper(), this);
		callbackHandler = new CallbackHandler();
	}


	/**
	 * Quits the server.
	 */
	public void quit() {
		serverHandler.removeCallbacks(null, null);
		serverHandler.getLooper().quit();
	}


	/**
	 * Executes the specified task.
	 * @param task
	 */
	public void execute(final Task task) {
		serverHandler.sendMessage(Message.obtain(serverHandler, 0, task));
	}


	/**
	 * Gets the SharedContext used by the server.
	 * @return
	 */
	public SharedContext getContext() {
		return context;
	}
}