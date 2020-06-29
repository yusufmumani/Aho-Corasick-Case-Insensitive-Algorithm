package com.venim;

import java.util.*;

class Node {
    Node parent;
    String key;
    char ch;
    List<Node> childLinks = new ArrayList<>();
    Map<Character, Node> childLinksMap = new HashMap<>();
    Node suffixLink;
    Node dictionaryLink;
    boolean isRoot, inDict;

    public void addChildLink(Node node) {
        childLinks.add(node);
        childLinksMap.put(Character.toLowerCase(node.ch), node);
    }

    Node(char ch) {
        this.ch = ch;
    }

    public void setIsRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }
}

public class Main {
    static String input = "Fuck you money";
    static Node root;
    static String[] keys = {"MONeY"};

    public static void main(String[] args) {
        root = new Node('\0');
        root.setIsRoot(true);
        for (String key : keys) {
            createChildLinks(root, key, 0);
        }
        createSuffixAndDictLinks(root);
        search(input);
    }

    public static void createChildLinks(Node node, String key, int pos) {
        if (key.length() == pos)
            return;
        for (Node child : node.childLinks) {
            if (child.ch == key.charAt(pos)) {
                createChildLinks(child, key, pos + 1);
                return;
            }
        }

        Node child = new Node(key.charAt(pos));
        child.inDict = pos == key.length() - 1;
        if (child.inDict) {
            child.key = key;
        }
        node.addChildLink(child);
        createChildLinks(child, key, pos + 1);
    }

    public static void createSuffixAndDictLinks(Node root) {
        Queue<Node> Q = new LinkedList<>();
        Q.offer(root);
        while (!Q.isEmpty()) {
            Node v = Q.poll();
            Node n;
            if (!v.isRoot) {
                if (v.parent == root) {
                    v.suffixLink = root;
                } else {
                    n = v.parent.suffixLink;
                    while (!isMyParentOrRoot(n, v)) {
                        n = n.suffixLink;
                    }
                }

                n = v;
                Node m = n.suffixLink;
                if (m.inDict)
                    n.dictionaryLink = m;
                else
                    n.dictionaryLink = m.dictionaryLink;
            }

            for (Node w : v.childLinks) {
                w.parent = v;
                Q.offer(w);
            }
        }
    }

    public static boolean isMyParentOrRoot(Node suffixLink, Node me) {
        for (Node child : suffixLink.childLinks) {
            if (child.ch == me.ch) {
                me.suffixLink = child;
                return true;
            }
        }
        if (suffixLink.isRoot) {
            me.suffixLink = suffixLink;
            return true;
        }
        return false;
    }

    public static void search(String string) {
        Node curr = root;
        for (int pos = 0; pos < string.length(); pos++) {
            char chAtPos = string.charAt(pos);

            if (curr.childLinksMap.containsKey(Character.toLowerCase(chAtPos))) {
                curr = curr.childLinksMap.get(Character.toLowerCase(chAtPos));
                if (curr.inDict) {
                    System.out.println(curr.key + " at end pos " + pos);
                }
                Node dictionaryLink = curr.dictionaryLink;
                while (dictionaryLink != null) {
                    System.out.println(dictionaryLink.key + " at end pos " + pos);
                    dictionaryLink = dictionaryLink.dictionaryLink;
                }
            } else {
                if (!curr.isRoot && curr.suffixLink != null) {
                    curr = curr.suffixLink;
                    pos--;
                }
            }
        }
    }
}
