## About sm2csv
Schneider Electric SoMachine to CSV tags converter.

Supported CSV targets:
- Weintek Easybuilder
- Siemens WinCC

## Install
### Windows
Put `sm2csv.exe` somewhere in your `PATH` or folder containing input files.

### Any platform supported by Java
Put `sm2csv.jar` in folder containing input files.

## Usage
1. copy configured variables from SoMachine IDE
2. paste to text editor
3. save with `utf-8` encoding and `.var` extention
4. create as many files as required
5. put these files to some `workDir`
6. from within `workDir` execute `sm2csv` command, alternatively: `java -jar sm2csv.jar`
7. output `.csv` files for all targets will be created in `workDir`

## Build
This is pure gradle project, java development kit is the only requirement. On windows just run `build.bat`, on other platforms `./gradlew`. Compiled binaries are placed in `sbin` folder.

## Development without IDE
If you want to change and run a program without IDE there is helper `run` task with customized working directory. This means yout can put input files to folder with name `work` and run program with gradle: `./gradlew run`.

## Contribute
Feel free to open a pull request/issue or fork this repo and submit your changes via a pull request.

## License
TODO
