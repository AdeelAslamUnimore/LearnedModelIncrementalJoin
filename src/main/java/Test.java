import com.conventionalindexes.bplustree.BPlusTree;
import com.learnedmodel.learnedindex.MergingToLearnedModel;
import org.apache.commons.math3.random.EmpiricalDistribution;

public class Test {
    public static void main(String[] args) {
        MergingToLearnedModel mergingToLearnedModel= new MergingToLearnedModel();
        BPlusTree bPlusTree= new BPlusTree();
        bPlusTree.initialize(4);
        bPlusTree.insert(12.3, "First Index");
        bPlusTree.insert(7.4,"Second Index");
        bPlusTree.insert(5.5,"Third Index");
        bPlusTree.insert(16.6,"Fourth Index");
        bPlusTree.insert(10.8,"Fifth Index");
        bPlusTree.insert(43.7,"Sixth Index");
        bPlusTree.insert(17.4,"Second Index");
        bPlusTree.insert(55.5,"Third Index");
        bPlusTree.insert(66.6,"Fourth Index");
        bPlusTree.insert(18.8,"Fifth Index");
        bPlusTree.insert(33.7,"Sixth Index");
        bPlusTree.insert(7.4,"Second Index");
        bPlusTree.insert(5.5,"Third Index");
        bPlusTree.insert(16.6,"Fourth Index");
        bPlusTree.insert(18.8,"Fifth Index");
        bPlusTree.insert(53.7,"Sixth Index");
        bPlusTree.insert(77.4,"Second Index");
        bPlusTree.insert(65.5,"Third Index");
        bPlusTree.insert(66.6,"Fourth Index");
        bPlusTree.insert(48.8,"Fifth Index");
        bPlusTree.insert(39.7,"Sixth Index");
        mergingToLearnedModel.empericalOptimizedCDF(bPlusTree,21);

    }
}