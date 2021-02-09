# ABSIMTH
A Hardware simulator written in Java.
This simulator enable to run many cpu and many task per cpu, allow to configurate the memory too.
This Simulate the use of [RV32I Base Instruction Set](https://content.riscv.org/wp-content/uploads/2017/05/riscv-spec-v2.2.pdf) (excluding EBREAK, CSR*, fence* and some environment calls)

# Environment Calls
This table content some environments call for you use in your C program
| ID `x10`    | Name         | Description                                            |
|-------------|--------------| -------------------------------------------------------|
| 1           | print_int    | Prints integer in `x11`                                |
| 4           | print_string | Prints null-terminated string whose address is in `x11`|
| 10          | exit         | Stops execution                                        |
| 11          | print_char   | Prints character in `x11`                              |

# Compiling and running
## Compile
Assuming current work directory:
```
gradlew build
```
This will generate a jar content everything you need.
### Run
```
java -jar build/lib/absimth.jar
```
## OpenJDK 11
As OpenJDK no longer supplies a runtime environment or JavaFX, it is required to have [OpenJFX](https://openjfx.io/) downloaded.
The path to OpenJFX will be referred to as `%PATH_TO_FX%`.
