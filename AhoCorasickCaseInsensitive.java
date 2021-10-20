package com.venim;

import java.util.*;

class Node {
    Node parent;
    String key;
    Character ch;
    List<Node> childLinks = new ArrayList<>();
    Map<Character, Node> childLinksMap = new HashMap<>();
    Node suffixLink;
    Node dictionaryLink;
    boolean isRoot, inDict;

    public void addChildLink(Node node) {
        childLinks.add(node);
        childLinksMap.put(Character.toLowerCase(node.ch), node);
    }

    Node(Character ch) {
        this.ch = ch;
        this.isRoot = ch == null;
    }
}

public class AhoCorasickCaseInsensitive {
    Node root;

    public static void main(String[] args) {
        AhoCorasickCaseInsensitive ahoCorasickCaseInsensitive = new AhoCorasickCaseInsensitive();
        ahoCorasickCaseInsensitive.query("MO Ne y NeY");
        ahoCorasickCaseInsensitive.search("Fuck you money").stream()
                .flatMap(List::stream)
                .map(String::valueOf)
                .forEach(System.out::println);
    }

    public void query(String query) {
        root = new Node(null);
        String[] keys = query.split("\\s+");
        for (String key : keys) {
            createChildLinks(root, key, 0);
        }
        createSuffixAndDictLinks(root);
    }

    public void createChildLinks(Node node, String key, int pos) {
        if (key.length() == pos)
            return;
        Node child = node.childLinksMap.get(Character.toLowerCase(key.charAt(pos)));
        if (child == null) {
            child = new Node(key.charAt(pos));
            node.addChildLink(child);
        }
        if (!child.inDict) {
            child.inDict = pos == key.length() - 1;
            child.key = key;
        }
        createChildLinks(child, key, pos + 1);
    }

    public void createSuffixAndDictLinks(Node root) {
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

    public boolean isMyParentOrRoot(Node suffixLink, Node me) {
        Node child = suffixLink.childLinksMap.get(Character.toLowerCase(me.ch));
        if (child != null) {
            me.suffixLink = child;
            return true;
        }
        if (suffixLink.isRoot) {
            me.suffixLink = suffixLink;
            return true;
        }
        return false;
    }

    public List<List<List>> search(String string) {
        List<List<List>> results = new ArrayList<>();
        Node curr = root;
        for (int pos = 0; pos < string.length(); pos++) {
            char chAtPos = string.charAt(pos);
            if (curr.childLinksMap.containsKey(Character.toLowerCase(chAtPos))) {
                curr = curr.childLinksMap.get(Character.toLowerCase(chAtPos));
                if (curr.inDict) {
                    List<Object> list1 = new ArrayList<>();
                    list1.add(pos - curr.key.length() + 1);
                    list1.add(pos);
                    
                    List<Object> list2 = new ArrayList<>();
                    list2.add(string.substring(pos - curr.key.length() + 1, pos + 1));

                    List<List> list3 = new ArrayList<>();
                    list3.add(list1);
                    list3.add(list2);
                    results.add(list3);
                }

                Node dictionaryLink = curr.dictionaryLink;
                while (dictionaryLink != null) {
                    List<Object> list1 = new ArrayList<>();
                    list1.add(pos - dictionaryLink.key.length() + 1);
                    list1.add(pos);

                    List<Object> list2 = new ArrayList<>();
                    list2.add(string.substring(pos - dictionaryLink.key.length() + 1, pos + 1));

                    List<List> list3 = new ArrayList<>();
                    list3.add(list1);
                    list3.add(list2);
                    results.add(list3);

                    dictionaryLink = dictionaryLink.dictionaryLink;
                }
            } else {
                if (!curr.isRoot && curr.suffixLink != null) {
                    curr = curr.suffixLink;
                    pos--;
                }
            }
        }
        // Absorb overlaps
        Map<Integer, Boolean> toBeRemoved = new HashMap<>();
        List<Integer> toBeRemovedArr = new ArrayList<>();
        for (int i = 0; i < results.size(); i++)
            for (int j = 0; j < results.size(); j++)
                if (i != j) {
                    Integer startI = (Integer) results.get(i).get(0).get(0);
                    Integer endI = (Integer) results.get(i).get(0).get(1);
                    Integer startJ = (Integer) results.get(j).get(0).get(0);
                    Integer endJ = (Integer) results.get(j).get(0).get(1);
                    if (startJ >= startI && endJ <= endI)
                        if (toBeRemoved.get(j) == null || !toBeRemoved.get(j)) {
                            toBeRemoved.put(j, true);
                            toBeRemovedArr.add(j);
                        }
                }

        if (toBeRemovedArr.size() > 0) {
            Collections.sort(toBeRemovedArr, Collections.reverseOrder());
            for (Integer i : toBeRemovedArr)
                results.remove(i.intValue());
        }
        return results;
    }
}
