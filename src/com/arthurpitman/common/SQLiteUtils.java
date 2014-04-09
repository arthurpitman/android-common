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

package com.arthurpitman.common;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;


/**
 * Utility class for SQLite databases.
 */
public class SQLiteUtils {

	/**
	 * Compile a create statement.
	 * @param db
	 * @param tableName
	 * @param idColumn
	 * @return
	 */
	public static SQLiteStatement compileCreateStatement(SQLiteDatabase db, String tableName, String idColumn) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ");
		sql.append(tableName);
		sql.append(" (");
		sql.append(idColumn);
		sql.append(") VALUES (NULL)");
		return db.compileStatement(sql.toString());
	}


	/**
	 * Compile an update statement.
	 * @param db
	 * @param tableName
	 * @param columns
	 * @return
	 */
	public static SQLiteStatement compileUpdateStatement(SQLiteDatabase db, String tableName, String[] columns) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT OR REPLACE INTO ");
		sql.append(tableName);
		sql.append(" (");

		for (int i = 0; i < columns.length; i++) {
			if (i > 0) {
				sql.append(", ");
			}
			sql.append(columns[i]);
		}
		sql.append(") VALUES (");
		for (int i = 0; i < columns.length; i++) {
			if (i == 0) {
				sql.append("?");
			} else {
				sql.append(", ?");
			}
		}
		sql.append(")");
		return db.compileStatement(sql.toString());
	}


	/**
	 * Compile a delete statement.
	 * @param db
	 * @param tableName
	 * @param keyColumn
	 * @return
	 */
	public static SQLiteStatement compileDeleteStatement(SQLiteDatabase db, String tableName, String keyColumn) {
		StringBuilder sql = new StringBuilder();
		sql.append("DELETE FROM ");
		sql.append(tableName);
		sql.append(" WHERE ");
		sql.append(keyColumn);
		sql.append("=?");
		return db.compileStatement(sql.toString());
	}


	/**
	 * Compile a clear statement.
	 * @param db
	 * @param tableName
	 * @return
	 */
	public static SQLiteStatement compileClearStatement(SQLiteDatabase db, String tableName) {
		StringBuilder sql = new StringBuilder();
		sql.append("DELETE FROM ");
		sql.append(tableName);
		return db.compileStatement(sql.toString());
	}


	/**
	 * Binds a String to a statement if possible.
	 * @param statement
	 * @param index
	 * @param value
	 */
	public static void bindStringToStatement(SQLiteStatement statement, int index, String value) {
		if (value != null) {
			statement.bindString(index, value);
		}
	}
}