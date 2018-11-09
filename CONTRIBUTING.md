### Contents
- [Project Setup](#project-setup)
- [Branching Conventions](#branching-conventions)
- [Commit Message Conventions](#commit-message-conventions)

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

Happy Coding !!!
