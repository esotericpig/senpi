# Contributing to senpi

Before contributing to **senpi**, always check the open and **closed** [Issues](https://github.com/esotericpig/senpi/issues) and [Pull Requests](https://github.com/esotericpig/senpi/pulls) first, as it may have already been resolved/added.

## Bug? Enhancement?

If it is a Bug or an Enhancement and you don't feel like making a pull request, please create an [Issue](https://github.com/esotericpig/senpi/issues) with the appropriate Label.  If it is related to a certain version, build environment, OS, etc., please include this information.  Thanks.

You may also mention and/or assign it to @esotericpig.

If your request is turned down, please take it gracefully, and you can always fork the project!

## Minor change?

If it is a minor change, such as fixing whitespace or a spelling error, always make an [Issue](https://github.com/esotericpig/senpi/issues) for this and please add **Minor:** to the beginning of the title, as well as a **minor** Label if possible.

Pull requests related to minor issues will probably be unaccepted.

## Pull request?

If you believe that you have solved a non-minor bug and/or have made a non-minor enhancement, please create a pull request.  If the bug is in master, create it against master.  If the bug is only in a certain version, create it against that version.

To create a pull request, first, fork the project.  Then commit your changes using a descriptive name for your branch.  Finally, create a pull request.  See [here](https://help.github.com/articles/creating-a-pull-request-from-a-fork/) for more information.

Please adhere to the following source code guidelines:
- For the most part, use standard Java guidelines (like curly brace on the same line as the method), except for the exceptions below.
- Use UTF-8 with Linux line endings ('\n', **not** '\r\n' or '\r') (Notepad++ on Windows makes this easy).
- Only use 2 spaces for indents.  Do **not** use tabs.
- Use a right margin of 110 (**not** 80), as most modern monitors are wide, and 80 is an underutilization of this extra space.
- Do **not** put spaces between commas:
  - doMethod(0,0,0), **not** doMethod(0, 0, 0)
- Classes (Interfaces) should follow this hierarchry:
  1. package
  2. static imports
  3. imports (grouped alphabetically by package)
  4. serialVersionUID
  5. constants (public, protected, private) (sorted alphabetically)
  6. variables (public, protected, private) (sorted alphabetically)
  7. abstract methods (sorted alphabetically)
  8. constructors (alphabetically by param names)
  9. methods (sorted alphabetically)
  10. setters (alphabetically by variable name)
  11. getters (alphabetically by variable name; #isHappy() comes before #getSad())
  12. to* methods (#toString(), etc.)
  13. aliases for other methods (#bib(), etc.)
  14. inner classes (public, protected, private)

There are exceptions to these rules.  Look through the current source files for a general idea.

If you don't feel like following these guidelines exactly, that is okay.  I will still potentially merge your changes and edit the files afterwards appropriately.

Quirky comments and/or identifiers are acceptable.

Thanks for contributing to **senpi**!  
:bow: ありがとうございます、  
:bow: 谢谢您，  
〜 The **senpi** Team  
