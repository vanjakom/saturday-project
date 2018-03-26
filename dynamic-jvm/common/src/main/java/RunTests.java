import com.mungolab.djvm.common.TestUtils;

/**
 * @author Vanja Komadinovic ( vanja@vast.com )
 */
public class RunTests {
    public static void main(String[] args) throws Exception {

        System.out.println("Running tests");

        TestUtils.runTests(
                "com.mungolab.djvm.common.LanguageUtils",
                "com.mungolab.djvm.common.PathUtils",
                "com.mungolab.djvm.common.RuntimeUtils",
                "com.mungolab.djvm.common.ClassLoaderUtils"
        );
    }
}
