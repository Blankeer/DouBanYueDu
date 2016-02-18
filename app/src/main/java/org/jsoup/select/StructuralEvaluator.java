package org.jsoup.select;

import java.util.Iterator;
import org.jsoup.nodes.Element;

abstract class StructuralEvaluator extends Evaluator {
    Evaluator evaluator;

    static class Root extends Evaluator {
        Root() {
        }

        public boolean matches(Element root, Element element) {
            return root == element;
        }
    }

    static class Has extends StructuralEvaluator {
        public Has(Evaluator evaluator) {
            this.evaluator = evaluator;
        }

        public boolean matches(Element root, Element element) {
            Iterator it = element.getAllElements().iterator();
            while (it.hasNext()) {
                Element e = (Element) it.next();
                if (e != element && this.evaluator.matches(root, e)) {
                    return true;
                }
            }
            return false;
        }

        public String toString() {
            return String.format(":has(%s)", new Object[]{this.evaluator});
        }
    }

    static class ImmediateParent extends StructuralEvaluator {
        public ImmediateParent(Evaluator evaluator) {
            this.evaluator = evaluator;
        }

        public boolean matches(Element root, Element element) {
            if (root == element) {
                return false;
            }
            Element parent = element.parent();
            if (parent == null || !this.evaluator.matches(root, parent)) {
                return false;
            }
            return true;
        }

        public String toString() {
            return String.format(":ImmediateParent%s", new Object[]{this.evaluator});
        }
    }

    static class ImmediatePreviousSibling extends StructuralEvaluator {
        public ImmediatePreviousSibling(Evaluator evaluator) {
            this.evaluator = evaluator;
        }

        public boolean matches(Element root, Element element) {
            if (root == element) {
                return false;
            }
            Element prev = element.previousElementSibling();
            if (prev == null || !this.evaluator.matches(root, prev)) {
                return false;
            }
            return true;
        }

        public String toString() {
            return String.format(":prev%s", new Object[]{this.evaluator});
        }
    }

    static class Not extends StructuralEvaluator {
        public Not(Evaluator evaluator) {
            this.evaluator = evaluator;
        }

        public boolean matches(Element root, Element node) {
            return !this.evaluator.matches(root, node);
        }

        public String toString() {
            return String.format(":not%s", new Object[]{this.evaluator});
        }
    }

    static class Parent extends StructuralEvaluator {
        public Parent(Evaluator evaluator) {
            this.evaluator = evaluator;
        }

        public boolean matches(Element root, Element element) {
            if (root == element) {
                return false;
            }
            for (Element parent = element.parent(); parent != root; parent = parent.parent()) {
                if (this.evaluator.matches(root, parent)) {
                    return true;
                }
            }
            return false;
        }

        public String toString() {
            return String.format(":parent%s", new Object[]{this.evaluator});
        }
    }

    static class PreviousSibling extends StructuralEvaluator {
        public PreviousSibling(Evaluator evaluator) {
            this.evaluator = evaluator;
        }

        public boolean matches(Element root, Element element) {
            if (root == element) {
                return false;
            }
            for (Element prev = element.previousElementSibling(); prev != null; prev = prev.previousElementSibling()) {
                if (this.evaluator.matches(root, prev)) {
                    return true;
                }
            }
            return false;
        }

        public String toString() {
            return String.format(":prev*%s", new Object[]{this.evaluator});
        }
    }

    StructuralEvaluator() {
    }
}
