package FP_growth;

import java.util.*;

public class TreeNode implements Comparable<TreeNode> {
    String ID;
    int count;
    TreeNode NextSameIDlink = null;
    TreeNode parent;
    List<TreeNode> child=new ArrayList<>();

    TreeNode(String ID, int count, TreeNode parent) {
        this.ID = ID;
        this.count = count;
        this.parent = parent;
    }

    static TreeNode creatTree(List<List<String>> OrderTL, Map<String,TreeNode>HeaderTable) {
        TreeNode root = new TreeNode("root", -1, null);
        for (List<String> al : OrderTL) {
            TreeNode parentNode = root;
            for (String id : al) {
                boolean temp = true;
                for (TreeNode check : parentNode.child)
                    if (check.ID.equals(id)) {
                        check.count++;
                        parentNode = check;
                        temp = false;
                        break;
                    }
                if (!temp)
                    continue;
                TreeNode node = new TreeNode(id, 1, parentNode);
                parentNode.child.add(node);
                parentNode = node;
                node.NextSameIDlink = HeaderTable.get(id);
                HeaderTable.put(id, node);
            }
        }
        return root;
    }

    static LinkedList<String> sortTL(List<String> TL, ArrayList<TreeNode> HTnode) {
        Map<String, Integer> map = new HashMap<>();
        for (String id : TL)
            for (int i = 0; i < HTnode.size(); i++) {
                TreeNode node = HTnode.get(i);
                if (node.ID.equals(id))
                    map.put(id, i);
            }
        List<Map.Entry<String, Integer>> temp = new ArrayList<>(map.entrySet());
        temp.sort(Map.Entry.comparingByValue());
        Collections.reverse(temp);
        LinkedList<String> orderTL = new LinkedList<>();
        for (Map.Entry<String, Integer> entry : temp)
            orderTL.add(entry.getKey());
        return orderTL;
    }
    static void creatTree2(List<List<String>> TL, ArrayList<TreeNode> HTnode) {
        TreeNode TreeRoot = new TreeNode("root", -1, null);
        for (List<String> transaction : TL) {
            LinkedList<String> orderTL = sortTL(transaction, HTnode);
            TreeNode node = TreeRoot;
            TreeNode subNode;
            if (TreeRoot.child != null)
                while ((subNode = node.getChildID(orderTL.peek())) != null) {
                    subNode.count++;
                    node = subNode;
                    orderTL.poll();
                }
            addNode(node, orderTL, HTnode);
        }
    }
    static void addNode(TreeNode ParentNode, LinkedList<String> orderTL, ArrayList<TreeNode> HTnode) {
        while (orderTL.size() > 0) {
            String ID = orderTL.poll();
            TreeNode ChildeNode = new TreeNode(ID, 1, ParentNode);
            ParentNode.child.add(ChildeNode);
            for (TreeNode Hnode : HTnode)
                if (Hnode.ID.equals(ID)) {
                    while (Hnode.NextSameIDlink != null)
                        Hnode = Hnode.NextSameIDlink;
                    Hnode.NextSameIDlink = ChildeNode;
                    break;
                }
            addNode(ChildeNode, orderTL, HTnode);
        }
    }

    public TreeNode getChildID(String ID) {
        List<TreeNode> childs = this.child;
        if (childs != null)
            for (TreeNode child : childs)
                if (child.ID.equals(ID))
                    return child;
        return null;
    }

    @Override
    public int compareTo(TreeNode arg0) {
        // TODO Auto-generated method stub
        int count0 = arg0.count;
        return count0 - this.count;
    }
}