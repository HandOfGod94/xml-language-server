### Contents
- [Project Setup](#project-setup)
- [Branching Conventions](#branching-conventions)
- [Commit Message Conventions](#commit-message-conventions)
    - [Full message format](#full-message-format)
    - [Revert](#revert)
    - [Type](#type)
    - [Scope](#scope)
    - [Subject](#subject)
- [Setup Dev Environment](#setup-dev-environment)
    - [Debugging environment](#debugging-environment)
    - [Generating Extension](#generating-extension)

## Project Setup
* `maven` version: 3.5.2
* `java` version: Open JDK 1.8

Make sure you are using correct `maven` version and required paths are added to classpath.
The project setup is quite simple. It's a standard `maven` project.
You can import the project into any ide such as `Eclipse` or `IntelliJ IDEA`.

`maven` will take care of resolving dependencies.
Both these IDE support importing of `maven` project directly.
Refer to their documentation to know more on how to import `maven` project in it.

To build project from command line you can use these commands as per your needs.
*project-dir* is the location where `pom.xml` is present.
```console
foo@bar:~/poject-dir $  mvn clean            # To clean up target directory
foo@bar:~/poject-dir $  mvn package          # To generate jar
foo@bar:~/poject-dir $  mvn install          # To generate jar and install it local maven repo
foo@bar:~/poject-dir $  mvn clean install    # Recommanded way to do a full build
```

## Branching Conventions
The issues can broadly classified into 3 categories.
1. Feature: A new functionality which needs or requested to be introduced.
2. Bug: An unexpected behavior of the functionality
3. Task: Routine maintenance task such as adding documentation, upgrading dependencies, verification of functionality etc.

For all the different types the branch name should be prefixed by `type` of issue plus the `id` of the issue
followed by `/` and then make sure the description is in `kebab-case`

```
<type>/<id>-<description-in-kebab-case>
```

For e.g. if you have following issues
* Feature: Add Custom Feature `#10`
* Bug: This is not working `#4`
* Task: Add more docs `#11`

then corresponding branch name will be like these

```
feat/10-add-custom-feature
bug/4-this-is-not-working
task/11-add-more-docs
```

## Commit Message Conventions
Commit message follows the `AngularJS` commit message conventions.
The conventions is highly standardized and helps in generating `CHANGELOG`
automatically.

```
/^(revert: )?(feat|fix|docs|style|refactor|perf|test|workflow|ci|chore|types)(\(.+\))?: .{1,50}/
```

For e.g. if you are working on a feature for autocomplete which you want to commit,
it may look like this

```
feat(autocomplete): list possible xml elements
```

### Full message format
Each commit message consists of a **header**, a **body** and a **footer**.
The header has a special format that includes a **type**, a **scope**
and a **subject**:

```
<type>(<scope>): <subject>
<BLANK LINE>
<body>
<BLANK LINE>
<footer>
```

The footer should contain a closing reference to an issue if any.

### Revert
If the commit reverts a previous commit, it should begin with `revert: `, followed by the header of the reverted commit. In the body it should say: `This reverts commit <hash>.`, where the hash is the SHA of the commit being reverted.

### Type
Must be one of the following:

* **build**: Changes that affect the build system or external dependencies (example scopes: gulp, broccoli, npm)
* **ci**: Changes to our CI configuration files and scripts.
* **docs**: Documentation only changes
* **feat**: A new feature
* **fix**: A bug fix
* **perf**: A code change that improves performance
* **refactor**: A code change that neither fixes a bug nor adds a feature
* **style**: Changes that do not affect the meaning of the code (white-space, formatting, missing semi-colons, etc)
* **test**: Adding missing tests or correcting existing tests
* **chore**: A task which needs to be carried out to improve quality of
    overall repository

### Scope
The following is the list of supported scopes:

* **autocomplete**
* **hover**
* **diagnostic**

The scope could be any major feature listing which you want to improve or want
to introduce. This felxblity allows creating new scopes.
**But make sure that if you are using a new scope, then don't forget to mention it
in the description of scope in PR or in issue comment.**

### Subject
The subject contains a succinct description of the change:

* use the imperative, present tense: "change" not "changed" nor "changes"
* don't capitalize the first letter
* no dot (.) at the end

## Setup Dev Environment

I strongly recommend all to go through once [Example-Language-Server](https://code.visualstudio.com/docs/extensions/example-language-server)
present on VSCode's official documentation to get idea on how a language
server and language client works.

A server requires a client to work. So [xml-language-server](https://github.com/HandOfGod94/xml-language-server)
requires [xml-vscode-plugin](https://github.com/HandOfGod94/xml-vscode-plugin) to work with. The server exposes
all the features such as validations, autocomplete, hover information and other IDE features as a set of `json-rpc`
endpoints defined in [language server specification](https://microsoft.github.io/language-server-protocol/specification).

### Debugging environment

1. Clone both the repo
   ```shell
   git clone https://github.com/HandOfGod94/xml-language-server.git   # clone language server
   git clone https://github.com/HandOfGod94/xml-vscode-plugin.git     # clone language client
   ```
   Both in 2 separate directory.

2. Install `VSCode`,`maven`, `java`, `node` and `npm`.  
   **VSCode Version:** 1.29.1  
   **Maven Version:** 3.5.2  
   **Java SDK Version:** 1.8  
   **Node Version:** v10.7.0  
   **npm Version:** 6.4.1  

   > These are the versions which I have used for development.  
   > Refer to their respective docs/websites to know how to install/setup it in local.

3. Generate language server jars  
   Now there are various ways in which you can generate jars.  
   The `xml-language-server` maven project contains various profiles to work with.

   ```shell
   # Just generate jar in "target" directory for maven
   mvn clean package

   # Genearte jar and install it in local .m2 repo without running tests and style checks
   # It by default points to "dev" profile
   mvn clean install

   # Generate jar, run tests and install it in local .m2 repo
   mvn clean install -Pdev-with-test

   # Generate jar, run tests, install, run style checks and generate coverage reports
   mvn clean install -Pprod
   ```

4. Open the [xml-vscode-client](https://github.com/HandOfGod94/xml-vscode-plugin) in VSCode.
5. Hit `F5` or go to `Debug` panel and click `Start` button.  
   > Note: This step assumes that you used any of the `install` command in maven and not `package` command.  
   > Also the the local .m2 repo is at `$HOME_DIR/.m2` 
   
   ![debug](resources/debug.png)

6. The current remote debugging port for Java is `8010`. You can connect from any ide
   to this port for Remote debugging.  
   If you want to change it then it can be modified in `xml-vscode-plugin/src/extension.ts` file
   ```typescript
   // Server options
    let serverOpts: ServerOptions = {
        run: {
        command: 'java',
        args:[
            '-Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager',
            '-jar',
            xmlServerPath
        ],
        options: { stdio: 'pipe'},
        transport: TransportKind.ipc,
        },
        debug: {
            command: 'java',
            args: [
                '-Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager',
                '-jar',
                
                //Modify the address to change the debugging port
                '-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,quiet=y,address=8010',
                debugXmlServerPath
            ],
            transport: TransportKind.ipc,
        }
    };
   ``` 

### Generating Extension

Steps 1-3 remains same as above. The only difference is that you can use `package` command also, which
was not the case in the former setup.

4. Once you have the jar, place it inside `xml-vscode-plugin/jars` directory.
5. Generate `.vsix` file
   ```shell
   vsce package
   ```
6. Install the `.vsix` file.  
   > Note.: This requires `code` command to be available in the system path  
   
   ```shell
   code --install-extension <path-to-vsix-file>
   ```
   
Now you will get extension in the vscode extension list.

Happy Coding !!!
