package com.phoenixx;

/**
 * @author Junaid Talpur
 * @project VugenThinker
 * @since 6:30 PM [20-02-2023]
 */
public class Testing {
    public static void main(String[] args) {
        String codeLine = "lr_think_time(thinkTime); //";
        int startIndex = codeLine.indexOf("lr_think_time(");
        startIndex +=14; // Size of the function name

        String foundVar = "";
        for(int i = 0; i < 30; i++) {
            char foundChar = codeLine.charAt(startIndex + i);
            if(Character.isAlphabetic(codeLine.charAt(startIndex + i))) {
                foundVar += foundChar;
            } else if(foundChar == ')') {
                break;
            }
        }

        System.out.println("FOUND: " + foundVar);

        //^\d{1,9}\d{1,2}$|^\d$
        /*Pattern pattern = Pattern.compile("lr_think_time\\(^[1-9]\\d{1,2}$|^\\d$\\);", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(codeLine);

        System.out.println(matcher);
        System.out.println("MATCHES; " + matcher.matches());
        System.out.println("FIND: " + matcher.find());

        //1st way
        Pattern p = Pattern.compile("foo");//. represents single character
        Matcher m = p.matcher("hello foo");
        boolean b = m.matches();
        System.out.println("SECOND MATCH: " + m.find());

        // TEST With index

        int startIndex = codeLine.indexOf("lr_think_time(");
        startIndex +=14; // Size of the function name

        System.out.println("STARTING INDEX: " + startIndex);

        String timeAmount = "";
        for(int i = 0; i < 6; i++) {
            char foundChar = codeLine.charAt(startIndex + i);
            if(Character.isDigit(codeLine.charAt(startIndex + i))) {
                timeAmount += foundChar;
            } else if(foundChar == ')') {
                break;
            }
        }

        System.out.println("TIME TOTAL: " + timeAmount);

        int commentIndex = codeLine.indexOf("//");
        boolean containsComment = codeLine.contains("//");
        System.out.println("COMMENT INDEX: " + commentIndex);
        System.out.println("CONTAINS COMMENT: " + containsComment);

        boolean isCommentAfter = codeLine.indexOf("//") > codeLine.indexOf("lr_think_time");
        System.out.println("COMMENT AFTER: " + isCommentAfter);

        boolean commentedOut = containsComment && !isCommentAfter;
        System.out.println("COMMENTED OUT: " + commentedOut);*/
    }
}
