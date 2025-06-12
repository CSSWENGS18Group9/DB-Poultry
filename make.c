#include <stdlib.h>
#include <string.h>

void build_class()
{
    if (system("gradlew clean build") != 0) exit(1);
}

void run_clean()
{
    if (system("gradlew clean") != 0) exit(1);
}

void build_fat_jar()
{
    if (system("gradlew jar") != 0) exit(1);
}

void ship()
{
    build_class();
    build_fat_jar();

    const char *pack =
        "jpackage "
        "--name DB-Poultry "
        "--vendor \"DB-Poultry DBMS Team\" "
        "--app-version 1.0 "
        "--input app/build/libs "
        "--main-jar app-1.0.jar "
        "--main-class org.db_poultry.AppKt "
        "--type exe "
        // "--icon installer/app-icon.png "
        "--dest shipping";

    if (system(pack) != 0) exit(1);
}

int main(int argc, char *argv[])
{
    if (argc < 2) exit(1);

    if (strcmp(argv[1], "class") == 0)
        build_class();
    else if (strcmp(argv[1], "fat") == 0)
        build_fat_jar();
    else if (strcmp(argv[1], "run") == 0)
        run_clean();
    else if (strcmp(argv[1], "ship") == 0)
        ship();
    else
        exit(1);

    return 0;
}
