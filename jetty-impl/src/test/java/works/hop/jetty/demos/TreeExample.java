package works.hop.jetty.demos;

import org.junit.Test;

import java.util.LinkedList;
import java.util.Queue;

public class TreeExample {

    @Test
    public void insertNodes() {
        BinTree tree = new BinTree();
        tree.insert(4);
        tree.insert(5);
        tree.insert(3);
        tree.insert(2);
        tree.insert(1);
        int height = tree.height(tree.root);
        System.out.println("\nheight -> " + height);
        tree.printPostOrder(tree.root);
        System.out.println("\ndone post order");
        tree.printInOrder(tree.root);
        System.out.println("\ndone in order");
        tree.printPreOrder(tree.root);
        System.out.println("\ndone pre order");
        tree.printLevelOrder(tree.root);
        System.out.println("\ndone level order");
        int[] array = {10, 20, 30, 40, 50, 60};
        BinTree newTree = new BinTree();
        newTree.root = tree.fromSortedArray(array, 0, array.length - 1);
        newTree.printLevelOrder(newTree.root);
        System.out.println("\ndone level order");
    }

    static class Node {

        int data;
        Node left, right;

        public Node(int data) {
            this.data = data;
        }

        void insert(int value) {
            if (value < data) {
                if (left == null) {
                    left = new Node(value);
                } else {
                    left.insert(value);
                }
            } else {
                if (right == null) {
                    right = new Node(value);
                } else {
                    right.insert(value);
                }
            }
        }
    }

    static class AvlNode {
        int data;
        int height = 0;
        AvlNode left = null, right = null;

        AvlNode(int data) {
            this.data = data;
        }
    }

    static class BinTree {

        Node root = null;

        void insert(int value) {
            if (root == null) {
                root = new Node(value);
            } else {
                root.insert(value);
            }
        }

        int height(Node node) {
            if (node == null) {
                return 0;
            } else {
                int lh = height(node.left);
                int rh = height(node.right);
                return (lh > rh) ? lh + 1 : rh + 1;
            }
        }

        void printPostOrder(Node node) {
            if (node == null) {
                return;
            }
            printPostOrder(node.left);
            printPostOrder(node.right);
            System.out.print(node.data + " ");
        }

        void printInOrder(Node node) {
            if (node == null) {
                return;
            }
            printInOrder(node.left);
            System.out.print(node.data + " ");
            printInOrder(node.right);
        }

        void printPreOrder(Node node) {
            if (node == null) {
                return;
            }
            System.out.print(node.data + " ");
            printPreOrder(node.left);
            printPreOrder(node.right);
        }

        void printLevelOrder(Node root) {
            Queue<Node> queue = new LinkedList<>();
            queue.add(root);
            while (!queue.isEmpty()) {
                Node node = queue.poll();
                System.out.print(node.data + " ");
                if (node.left != null) {
                    queue.add(node.left);
                }
                if (node.right != null) {
                    queue.add(node.right);
                }
            }
        }

        Node fromSortedArray(int[] array, int start, int end) {
            if (start > end) {
                return null;
            }
            int mid = (start + end) / 2;
            Node root = new Node(array[mid]);
            root.left = fromSortedArray(array, start, mid - 1);
            root.right = fromSortedArray(array, mid + 1, end);
            return root;
        }
    }

    static class AvlTree {

        AvlNode root = null;

        boolean isEmpty() {
            return root == null;
        }


    }
}
