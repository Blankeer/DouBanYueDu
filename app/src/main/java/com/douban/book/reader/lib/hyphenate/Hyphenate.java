package com.douban.book.reader.lib.hyphenate;

import com.douban.book.reader.util.Logger;
import io.realm.internal.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Hyphenate {
    public static final String TAG = "Hyphenate";
    private static Map<String, ArrayList<Integer>> sExceptionsMap;
    private static Tree sHyphenTree;

    private static class Node {
        public ArrayList<Node> children;
        public String data;
        public Node parent;

        public Node(String data) {
            this.children = new ArrayList();
            this.data = data;
        }

        public Node createChildNode(Node childNode) {
            childNode.parent = this;
            this.children.add(childNode);
            return childNode;
        }
    }

    private static class Tree {
        private Node root;

        public Node getRoot() {
            return this.root;
        }

        public void setRoot(Node root) {
            this.root = root;
        }

        public Tree(String rootData) {
            this.root = new Node(rootData);
        }
    }

    static {
        sExceptionsMap = null;
        sHyphenTree = null;
    }

    public static void initPatterns() {
        makeExceptionMap();
        makePatterns();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.ArrayList<java.lang.String> hyphenateWord(java.lang.String r27) {
        /*
        initPatterns();
        r18 = new java.util.ArrayList;
        r18.<init>();
        r24 = r27.length();
        r25 = 4;
        r0 = r24;
        r1 = r25;
        if (r0 > r1) goto L_0x001c;
    L_0x0014:
        r0 = r18;
        r1 = r27;
        r0.add(r1);
    L_0x001b:
        return r18;
    L_0x001c:
        r23 = r27.toLowerCase();
        r24 = sExceptionsMap;
        r0 = r24;
        r1 = r23;
        r24 = r0.containsKey(r1);
        if (r24 == 0) goto L_0x005d;
    L_0x002c:
        r24 = sExceptionsMap;
        r0 = r24;
        r1 = r23;
        r16 = r0.get(r1);
        r16 = (java.util.ArrayList) r16;
        r24 = r16.size();
        r0 = r24;
        r0 = new int[r0];
        r17 = r0;
        r6 = 0;
    L_0x0043:
        r0 = r17;
        r0 = r0.length;
        r24 = r0;
        r0 = r24;
        if (r6 >= r0) goto L_0x0153;
    L_0x004c:
        r0 = r16;
        r24 = r0.get(r6);
        r24 = (java.lang.Integer) r24;
        r24 = r24.intValue();
        r17[r6] = r24;
        r6 = r6 + 1;
        goto L_0x0043;
    L_0x005d:
        r24 = new java.lang.StringBuilder;
        r24.<init>();
        r25 = ".";
        r24 = r24.append(r25);
        r0 = r24;
        r1 = r23;
        r24 = r0.append(r1);
        r25 = ".";
        r24 = r24.append(r25);
        r23 = r24.toString();
        r24 = r23.length();
        r24 = r24 + 1;
        r0 = r24;
        r0 = new int[r0];
        r17 = r0;
        r6 = 0;
    L_0x0087:
        r24 = r23.length();
        r0 = r24;
        if (r6 >= r0) goto L_0x0138;
    L_0x008f:
        r24 = sHyphenTree;
        r4 = r24.getRoot();
        r0 = r23;
        r15 = r0.substring(r6);
        r8 = 0;
    L_0x009c:
        r24 = r15.length();
        r0 = r24;
        if (r8 >= r0) goto L_0x0134;
    L_0x00a4:
        r24 = r8 + 1;
        r0 = r24;
        r2 = r15.substring(r8, r0);
        r5 = 0;
        r3 = r4.children;
        r9 = 0;
    L_0x00b0:
        r24 = r3.size();
        r0 = r24;
        if (r9 >= r0) goto L_0x00cf;
    L_0x00b8:
        r20 = r3.get(r9);
        r20 = (com.douban.book.reader.lib.hyphenate.Hyphenate.Node) r20;
        r0 = r20;
        r0 = r0.data;
        r24 = r0;
        r0 = r24;
        r24 = r0.equals(r2);
        if (r24 == 0) goto L_0x012a;
    L_0x00cc:
        r4 = r20;
        r5 = 1;
    L_0x00cf:
        if (r5 == 0) goto L_0x0134;
    L_0x00d1:
        r0 = r4.children;
        r21 = r0;
        r12 = 0;
    L_0x00d6:
        r24 = r21.size();
        r0 = r24;
        if (r12 >= r0) goto L_0x0130;
    L_0x00de:
        r0 = r21;
        r22 = r0.get(r12);
        r22 = (com.douban.book.reader.lib.hyphenate.Hyphenate.Node) r22;
        r0 = r22;
        r0 = r0.data;
        r24 = r0;
        r25 = ", ";
        r24 = r24.contains(r25);
        if (r24 == 0) goto L_0x012d;
    L_0x00f4:
        r0 = r22;
        r0 = r0.data;
        r24 = r0;
        r25 = ", ";
        r19 = r24.split(r25);
        r0 = r19;
        r0 = r0.length;
        r24 = r0;
        r0 = r24;
        r7 = new int[r0];
        r13 = 0;
    L_0x010a:
        r0 = r7.length;
        r24 = r0;
        r0 = r24;
        if (r13 >= r0) goto L_0x012d;
    L_0x0111:
        r24 = r19[r13];
        r24 = java.lang.Integer.parseInt(r24);
        r7[r13] = r24;
        r24 = r6 + r13;
        r25 = r6 + r13;
        r25 = r17[r25];
        r26 = r7[r13];
        r25 = java.lang.Math.max(r25, r26);
        r17[r24] = r25;
        r13 = r13 + 1;
        goto L_0x010a;
    L_0x012a:
        r9 = r9 + 1;
        goto L_0x00b0;
    L_0x012d:
        r12 = r12 + 1;
        goto L_0x00d6;
    L_0x0130:
        r8 = r8 + 1;
        goto L_0x009c;
    L_0x0134:
        r6 = r6 + 1;
        goto L_0x0087;
    L_0x0138:
        r24 = 1;
        r25 = 0;
        r17[r24] = r25;
        r24 = 2;
        r25 = 0;
        r17[r24] = r25;
        r0 = r17;
        r11 = r0.length;
        r24 = r11 + -2;
        r25 = 0;
        r17[r24] = r25;
        r24 = r11 + -3;
        r25 = 0;
        r17[r24] = r25;
    L_0x0153:
        r10 = 0;
        r6 = 2;
        r8 = 0;
    L_0x0156:
        r0 = r17;
        r0 = r0.length;
        r24 = r0;
        r0 = r24;
        if (r6 >= r0) goto L_0x018b;
    L_0x015f:
        r24 = r27.length();
        r0 = r24;
        if (r8 >= r0) goto L_0x018b;
    L_0x0167:
        r14 = r17[r6];
        r24 = r14 % 2;
        r25 = 1;
        r0 = r24;
        r1 = r25;
        if (r0 != r1) goto L_0x0186;
    L_0x0173:
        r24 = r8 + 1;
        r0 = r27;
        r1 = r24;
        r24 = r0.substring(r10, r1);
        r0 = r18;
        r1 = r24;
        r0.add(r1);
        r10 = r8 + 1;
    L_0x0186:
        r6 = r6 + 1;
        r8 = r8 + 1;
        goto L_0x0156;
    L_0x018b:
        r0 = r27;
        r24 = r0.substring(r10);
        r0 = r18;
        r1 = r24;
        r0.add(r1);
        goto L_0x001b;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.douban.book.reader.lib.hyphenate.Hyphenate.hyphenateWord(java.lang.String):java.util.ArrayList<java.lang.String>");
    }

    private static void makeExceptionMap() {
        if (sExceptionsMap == null) {
            HashMap<String, ArrayList<Integer>> map = new HashMap();
            for (String exception : Patterns.exceptions) {
                String ex = exception.replace("-", Table.STRING_DEFAULT_VALUE);
                String[] chars = exception.split("[a-z]", -1);
                ArrayList<Integer> points = new ArrayList();
                points.add(Integer.valueOf(0));
                for (String equals : chars) {
                    if (equals.equals("-")) {
                        points.add(Integer.valueOf(1));
                    } else {
                        points.add(Integer.valueOf(0));
                    }
                }
                map.put(ex, points);
            }
            sExceptionsMap = map;
        }
    }

    private static void makePatterns() {
        if (sHyphenTree == null) {
            long before = System.currentTimeMillis();
            Tree hyphenateTree = new Tree("@");
            for (String pattern : Patterns.patterns) {
                String chars = pattern.replaceAll("[0-9]", Table.STRING_DEFAULT_VALUE);
                String[] strs = pattern.split("[.a-z]", -1);
                int[] points = new int[strs.length];
                int i = 0;
                while (true) {
                    int length = strs.length;
                    if (i >= r0) {
                        break;
                    }
                    if (strs[i].equals(Table.STRING_DEFAULT_VALUE)) {
                        points[i] = 0;
                    } else {
                        points[i] = Integer.parseInt(strs[i]);
                    }
                    i++;
                }
                Node currentNode = hyphenateTree.getRoot();
                boolean flag = false;
                for (i = 0; i < chars.length(); i++) {
                    String charStr = chars.substring(i, i + 1);
                    ArrayList<Node> childrenNodes = currentNode.children;
                    for (int j = 0; j < childrenNodes.size(); j++) {
                        Node tempNode = (Node) childrenNodes.get(j);
                        if (tempNode.data.equals(charStr)) {
                            currentNode = tempNode;
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        Node newNode = new Node(charStr);
                        currentNode.createChildNode(newNode);
                        currentNode = newNode;
                    }
                    flag = false;
                }
                Node leafNode = new Node(Arrays.toString(points));
                currentNode.createChildNode(leafNode);
                leafNode.data = leafNode.data.substring(1, leafNode.data.length() - 1);
            }
            Logger.d(TAG, "make pattern elapsed: " + (System.currentTimeMillis() - before), new Object[0]);
            sHyphenTree = hyphenateTree;
        }
    }
}
