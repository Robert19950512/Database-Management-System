package hw1;

import java.io.*;

/*
 * Student 1 name: Fa Long (id:462512)
 * Student 2 name: Zhuo Wei (id: 462473)
 * Date: Sep 13th, 2019
 */

/** Database is a class that initializes a static
    variable used by the database system (the catalog)

    Provides a set of methods that can be used to access these variables
    from anywhere.
 */

public class Database {
	private static Database _instance = new Database();
	private final Catalog _catalog;

	private Database() {
		_catalog = new Catalog();
	}

	/** Return the catalog of the static Database instance*/
	public static Catalog getCatalog() {
		return _instance._catalog;
	}


	//reset the database, used for unit tests only.
	public static void reset() {
		_instance = new Database();
	}

}