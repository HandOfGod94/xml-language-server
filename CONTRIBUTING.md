### Contents
- [Project Setup](#project-setup)
- [Branching Conventions](#branching-conventions)
- [Commit Message Conventions](#commit-message-conventions)

## Project Setup
* `maven` version: 3.5.2
* `java` version: Oracle JDK 1.8

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
Commit message also follows similar conventions of using a fix prefix based on the type.
> Make sure to include issue `#id` in all the commit message to reference it correctly to an issue, followed by comma.

```
<Type> #<id>, Regualr Commit Message Description
```

For e.g. if you are working on a feature for which you want to commit,
it may look like this

```
Feat #10, Added Custom feature
```
Notice the first letter in the message is in caps. i.e.
* for feature it will be `Feat`
* for bug it will be `Bug`
* for task it will be `Task`

> Note: This guideline might modify post release, but the branching and commit convention will remain same.

Happy Coding !!!
