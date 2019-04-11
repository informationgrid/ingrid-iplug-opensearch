OpenSearch iPlug
========

The OpenSearch iPlug connects an OpenSearch interface to the InGrid data space.

Features
--------

- connects an OpenSearch interface to the InGrid data space
- maps InGrid query fields to fields of the OpenSearch interface
- provides functionality to adjust the scores from the OpenSearch interface to the InGrid score values [0..1]
- GUI for easy administration


Requirements
-------------

- a running InGrid Software System

Installation
------------

Download from https://distributions.informationgrid.eu/ingrid-iplug-opensearch/
 
or

build from source with `mvn clean package`.

Execute

```
java -jar ingrid-iplug-opensearch-x.x.x-installer.jar
```

and follow the install instructions.

Obtain further information at http://www.ingrid-oss.eu/ (sorry only in German)


Contribute
----------

- Issue Tracker: https://github.com/informationgrid/ingrid-iplug-opensearch/issues
- Source Code: https://github.com/informationgrid/ingrid-iplug-opensearch
 
### Setup Eclipse project

* import project as Maven-Project
* right click on project and select Maven -> Select Maven Profiles ... (Ctrl+Alt+P)
* choose profile "development"
* run "mvn compile" from Commandline (unpacks base-webapp) 
* run de.ingrid.iplug.opensearch.OpenSearchPlug as Java Application
* in browser call "http://localhost:10013" with login "admin/admin"

### Setup IntelliJ IDEA project

* choose action "Add Maven Projects" and select pom.xml
* in Maven panel expand "Profiles" and make sure "development" is checked
* run de.ingrid.iplug.opensearch.OpenSearchPlug
* in browser call "http://localhost:10013" with login "admin/admin"

Support
-------

If you are having issues, please let us know: info@informationgrid.eu

License
-------

The project is licensed under the EUPL license.
