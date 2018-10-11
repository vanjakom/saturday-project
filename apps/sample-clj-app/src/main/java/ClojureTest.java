import clojure.java.api.Clojure;
import clojure.lang.Compile;
import clojure.lang.Compiler;
import clojure.lang.IFn;
import clojure.lang.RT;

import java.util.Map;


/**
 * @author Vanja Komadinovic ( vanja@vast.com )
 */
public class ClojureTest {
    public static void main(String[] args) throws Exception {
        Object simpleForm = Clojure.read("(println \"Hello World\")");
        Object simpleFnCode = Clojure.read("(fn [x] (+ x 1))");

        Object result = Compiler.eval(simpleForm);

        IFn simpleFn = (IFn)Compiler.eval(simpleFnCode);

        IFn simpleFnCopy = (IFn)simpleFn.getClass().newInstance();

        System.out.println("Result of copy " + simpleFnCopy.invoke(12));

        Map<String, Object> map = (Map<String, Object>)Compiler.eval(Clojure.read("{\"a\" \"b\"}"));

        System.out.println("key in map: " + map.get("a"));

        System.out.println("Result " + simpleFn.invoke(10));

        ClassLoader classLoader = RT.baseLoader();

        System.out.println("End");
    }
}
