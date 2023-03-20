# VugenThinker
A tool to automate the process of modifying Vugen script think times / synchronizing them for dozens of scripts at a time. Or in other words, an automation developers best friend.

## Installation
Installing this is extremely simple, head over to the relases tab of this repository, download the jar and place it into the folder containing the scripts you'd like to modify.

![Work_folder](https://user-images.githubusercontent.com/24358694/221370078-dc7e06eb-2f00-48d4-8287-5d62c237ca99.png)

## Usage
> **Note**
> Make sure to have a backup of the scripts!

Once you have the jar placed in the directory containing the scripts, open a command line of your choosing (CMD, Powershell, etc) in that same directory and
run ``java -jar <name of jar>``

![Terminal](https://user-images.githubusercontent.com/24358694/221370185-baafbda0-0206-4dbd-b792-a272aa734408.png)

This will launch the application and you will be presented with the following menu,
```java
======================= Vugen Thinker Main Menu =======================
Select an option:
0) Exit
1) View scripts in current folder
2) Settings
3) Apply changes
> 
```
<!--- ![image](https://user-images.githubusercontent.com/24358694/221370376-049fd9e2-32b7-44b9-bdc7-4db046e03bd4.png) -->

Since the first two options are pretty self explanatory, we'll cover the last two. 

### Settings
The ``Settings`` option will take you to the settings menu, in which you can specify the think time, enable/disable the runtime think time limit,
and include/exclude scripts from the program.
```java
====================== Settings Menu =====================
Select an option:
0) Return to main menu
1) Set think time
2) Enable / Disable think time limiter
3) Include Scripts
4) Exclude Scripts
5) View selected settings
>
```
Again, since the first 3 options are self explanatory, I will explain what the third and fourth options do.
In cases where you have a script folder containing dozens of scripts, but you only want to modify a handful, say, 8 out of 20.
You can choose to ``Include`` those specfic scripts. As seen below, you will be provided the scripts, along with their ``ID`` on the left hand side. 
The tool will allow you to select any of the scripts already loaded and then you can enter ``0`` to save.

![image](https://user-images.githubusercontent.com/24358694/221371024-3e5f53b0-0a03-412e-89d7-55a646a37ca0.png)

The fourth option ``Exclude Scripts``, works the same way. You'll be provided with a list of scripts, in which you can chose to ``Exclude`` from the program, such that,
they will NOT be modifed, but the rest will.

Once you are finished specfiying the settings, you can enter ``0`` to head back to the main menu.

### Apply Changes
The ``Apply Changes`` option will then update the given scripts with the chosen settings. You will be presented with a confirmation menu to finalize everything before
the tool modifies the scripts.

![image](https://user-images.githubusercontent.com/24358694/221371307-66eed517-418e-413d-85aa-4138b8a66edd.png)

And that's it. You've saved hours of work. Congratulations!
