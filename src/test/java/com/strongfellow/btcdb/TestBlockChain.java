package com.strongfellow.btcdb;

import java.io.IOException;
import java.security.DigestException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.strongfellow.btcdb.components.DBUtils;
import com.strongfellow.btcdb.components.WriteService;
import com.strongfellow.btcdb.components.QueryCache;
import com.strongfellow.btcdb.components.StrongfellowDB;
import com.strongfellow.btcdb.protocol.Block;
import com.strongfellow.btcdb.protocol.BlockHeader;
import com.strongfellow.btcdb.protocol.Metadata;

@Test
public class TestBlockChain {

    @BeforeSuite
    public void init() {
        DataSource ds = DBUtils.getSqliteDataSource("unit-test.db");
        StrongfellowDB db = new StrongfellowDB();
        db.setDataSource(ds);
        db.setQueryCache(new QueryCache());
        WriteService listener = new WriteService();
        listener.setDatabase(db);
        this.listener = listener;
    }

    private WriteService listener;

    private static class Node {

        public static Set<Node> getNodes() {
            return new HashSet<Node>(nodes.values());
        }

        private static Map<String, Node> nodes = new HashMap<String, Node>();

        private static void makeNode(String id, String parentId, int height, int depth, String... validTips) {
            if (!nodes.containsKey(id)) {
                nodes.put(id, new Node(id));
            }
            if (parentId != null) {
                if (!nodes.containsKey(parentId)) {
                    nodes.put(parentId, new Node(parentId));
                }
                nodes.get(parentId).children.add(nodes.get(id));
                nodes.get(id).parent = nodes.get(parentId);
            }

            nodes.get(id).height = height;
            nodes.get(id).depth = depth;

            for (String x : validTips) {
                if (!nodes.containsKey(x)) {
                    nodes.put(x, new Node(x));
                }
                nodes.get(id).validTips.add(nodes.get(x));
            }
        }

        private Node(String id) {
            this.id = id;
        }
        final String id;
        Node parent = null;
        int height;
        int depth;
        final Set<Node> validTips = new HashSet<Node>();
        final Set<Node> children = new HashSet<>();
    };

    static {
        Node.makeNode("A", "B", 8, 0, "A");
        Node.makeNode("B", "D", 7, 1, "A");
        Node.makeNode("C", "E", 6, 0, "C");
        Node.makeNode("D", "E", 6, 2, "A");
        Node.makeNode("E", "G", 5, 3, "A");
        Node.makeNode("a", "K", 4, 0, "a");
        Node.makeNode("b", "K", 4, 0, "b");
        Node.makeNode("c", "L", 4, 0, "c");
        Node.makeNode("d", "L", 4, 0, "d");
        Node.makeNode("F", "M", 4, 0, "F");
        Node.makeNode("G", "M", 4, 4, "A");
        Node.makeNode("H", "N", 4, 0, "H");
        Node.makeNode("I", "O", 4, 0, "I");
        Node.makeNode("J", "Q", 3, 0, "J");
        Node.makeNode("K", "R", 3, 1, "a", "b");
        Node.makeNode("L", "R", 3, 1, "c", "d");
        Node.makeNode("e", "R", 3, 0, "e");
        Node.makeNode("M", "S", 3, 5, "A");
        Node.makeNode("N", "T", 3, 1, "H");
        Node.makeNode("O", "U", 3, 1, "I");
        Node.makeNode("P", "W", 2, 0, "P");
        Node.makeNode("Q", "W", 2, 1, "J");
        Node.makeNode("R", "W", 2, 2, "a", "b", "c", "d");
        Node.makeNode("S", "W", 2, 6, "A");
        Node.makeNode("T", "X", 2, 2, "H");
        Node.makeNode("U", "Y", 2, 2, "I");
        Node.makeNode("V", "Z", 1, 0, "V");
        Node.makeNode("W", "Z", 1, 7, "A");
        Node.makeNode("X", "Z", 1, 3, "H");
        Node.makeNode("Y", "Z", 1, 3, "I");
        Node.makeNode("Z", null, 0, 8, "A");
    }

    public void testNodeTips() {
        for (Node n : Node.getNodes()) {

            // every node has a tip
            Assert.assertTrue(n.validTips.size() > 0, n.id);

            // the node can be its own tip
            if (n.depth == 0) {
                Assert.assertEquals(n.validTips, Arrays.asList(n));
            } else { // or it must not be
                Assert.assertFalse(n.validTips.contains(n));
                HashSet<Node> notFound = new HashSet<>(n.validTips);
                for (Node x : n.children) {
                    for (Node tip : x.validTips) {
                        if (notFound.contains(tip)) {
                            Assert.assertEquals(x.depth, n.depth  - 1);
                            notFound.remove(tip);
                        } else {
                            Assert.assertTrue(x.depth < n.depth - 1, "for node n=" + n.id + " we found child " + x.id);
                        }
                    }
                }
                Assert.assertTrue(notFound.isEmpty(), "we didnt find tips "
                        + StringUtils.join(notFound) + " for node " + n.id);
            }
        }
    }

    public void testHeights() {
        for (Node n : Node.getNodes()) {
            if (n.height == 0) {
                Assert.assertEquals(n.id, "Z");
            } else {
                Assert.assertEquals(n.height, n.parent.height + 1);
            }
        }
    }

    public void testDepths() {
        for (Node n : Node.getNodes()) {
            for (Node t : n.validTips) {
                Assert.assertEquals(n.depth, t.height - n.height);
            }
        }
    }

    public void testChain() throws DataAccessException, IOException, DigestException {
        for (int i = 0; i < 10000; i++) {
            List<Node> nodes = new ArrayList<>(Node.getNodes());
            Collections.shuffle(nodes);

            for (Node n : nodes) {
                insert(n);
            }
        }
    }

    private void insert(Node n) throws DataAccessException, IOException, DigestException {
        if (n.parent != null) {
            Metadata bm = new Metadata(0, n.id.getBytes());
            BlockHeader header = new BlockHeader(0, n.parent.id.getBytes(), null, 0l, 0l, 0l);
            Block b = new Block(bm, header, Collections.emptyList());
            this.listener.processBlock(b);
        }
    }

}
