## About sm2csv

Schneider Electric SoMachine to CSV tags converter.

Supported CSV targets:
- Weintek Easybuilder
- Siemens WinCC
- Simple-Scada
- Schneider Electric Vijeo Designer

## Install

### Windows

Put `sm2csv.jar` and `sm2csv.cmd` somewhere in your `PATH` or into some `workDir`.

### Any platform supported by Java

Put `sm2csv.jar` into some `workDir`.

## Usage

1. from within SoMachine IDE go through code generation procedure
2. copy generated symbol configuration file into `workDir` as `tags.xml`
3. open command prompt and change directory to `workDir`
6. execute command:

```
$ sm2csv
```

or:

```
$ java -jar sm2csv.jar
```

Generated files for all targets will be placed into `workDir`.

For more usage information see:

```
$ sm2csv --help
```

## Code generation procedure

1. Tools Tree -> Application -> Add object -> Symbol configuration
2. Symbol configuration -> Setting -> Include Comments in XML
3. Choose variables to be accessed over network by enabling checkboxes
4. Build -> Generate code

Generated `.xml` file will be placed into your project directory.

## Build

This is pure gradle project, java development kit is the only requirement. On windows just run `build.bat`, on other platforms `./gradlew`. Compiled binaries are placed in `sbin` folder.

## Development without IDE

If you want to change and run a program without IDE there is helper `run` task with customized working directory. This means yout can put input files to folder with name `work` and run program with gradle: `./gradlew run`.

## License

TODO
