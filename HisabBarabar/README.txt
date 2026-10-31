Hisab Barabar - Split expenses with friends (Java Swing)

Run (VS Code): open src/MainFrame.java and click "Run" above main().
Run (terminal):
    cd src
    javac *.java
    java MainFrame

Make a JAR to share:
    javac *.java
    jar cfe HisabBarabar.jar MainFrame *.class
    java -jar HisabBarabar.jar

Classes:
  Transaction (parent) -> Expense, Payment (children)  = INHERITANCE
  ColorButton extends JButton                          = INHERITANCE
  Person, MonthRecord: private fields + getters/setters = ENCAPSULATION
  FileHelper: saves/loads history.txt
  MainFrame: GUI + button logic
