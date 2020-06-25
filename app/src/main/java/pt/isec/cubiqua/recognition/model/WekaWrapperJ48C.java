package pt.isec.cubiqua.recognition.model;

// Generated with Weka 3.8.4
//
// This code is public domain and comes with no warranty.
//
// Timestamp: Thu Jun 25 17:01:14 BST 2020


import weka.classifiers.AbstractClassifier;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.RevisionUtils;

public class WekaWrapperJ48C
        extends AbstractClassifier {

    /**
     * Returns only the toString() method.
     *
     * @return a string describing the classifier
     */
    public String globalInfo() {
        return toString();
    }

    /**
     * Returns the capabilities of this classifier.
     *
     * @return the capabilities
     */
    public Capabilities getCapabilities() {
        weka.core.Capabilities result = new weka.core.Capabilities(this);

        result.enable(weka.core.Capabilities.Capability.NOMINAL_ATTRIBUTES);
        result.enableDependency(weka.core.Capabilities.Capability.NOMINAL_ATTRIBUTES);
        result.enable(weka.core.Capabilities.Capability.NUMERIC_ATTRIBUTES);
        result.enableDependency(weka.core.Capabilities.Capability.NUMERIC_ATTRIBUTES);
        result.enable(weka.core.Capabilities.Capability.DATE_ATTRIBUTES);
        result.enableDependency(weka.core.Capabilities.Capability.DATE_ATTRIBUTES);
        result.enable(weka.core.Capabilities.Capability.STRING_ATTRIBUTES);
        result.enableDependency(weka.core.Capabilities.Capability.STRING_ATTRIBUTES);
        result.enable(weka.core.Capabilities.Capability.RELATIONAL_ATTRIBUTES);
        result.enableDependency(weka.core.Capabilities.Capability.RELATIONAL_ATTRIBUTES);
        result.enable(weka.core.Capabilities.Capability.MISSING_VALUES);
        result.enable(weka.core.Capabilities.Capability.NOMINAL_CLASS);
        result.enableDependency(weka.core.Capabilities.Capability.NOMINAL_CLASS);
        result.enable(weka.core.Capabilities.Capability.UNARY_CLASS);
        result.enableDependency(weka.core.Capabilities.Capability.UNARY_CLASS);
        result.enable(weka.core.Capabilities.Capability.EMPTY_NOMINAL_CLASS);
        result.enableDependency(weka.core.Capabilities.Capability.EMPTY_NOMINAL_CLASS);
        result.enable(weka.core.Capabilities.Capability.NUMERIC_CLASS);
        result.enableDependency(weka.core.Capabilities.Capability.NUMERIC_CLASS);
        result.enable(weka.core.Capabilities.Capability.DATE_CLASS);
        result.enableDependency(weka.core.Capabilities.Capability.DATE_CLASS);
        result.enable(weka.core.Capabilities.Capability.STRING_CLASS);
        result.enableDependency(weka.core.Capabilities.Capability.STRING_CLASS);
        result.enable(weka.core.Capabilities.Capability.RELATIONAL_CLASS);
        result.enableDependency(weka.core.Capabilities.Capability.RELATIONAL_CLASS);
        result.enable(weka.core.Capabilities.Capability.MISSING_CLASS_VALUES);


        result.setMinimumNumberInstances(1);

        return result;
    }

    /**
     * only checks the data against its capabilities.
     *
     * @param i the training data
     */
    public void buildClassifier(Instances i) throws Exception {
        // can classifier handle the data?
        getCapabilities().testWithFail(i);
    }

    /**
     * Classifies the given instance.
     *
     * @param i the instance to classify
     * @return the classification result
     */
    public double classifyInstance(Instance i) throws Exception {
        Object[] s = new Object[i.numAttributes()];

        for (int j = 0; j < s.length; j++) {
            if (!i.isMissing(j)) {
                if (i.attribute(j).isNominal())
                    s[j] = new String(i.stringValue(j));
                else if (i.attribute(j).isNumeric())
                    s[j] = new Double(i.value(j));
            }
        }

        // set class value to missing
        s[i.classIndex()] = null;

        return WekaClassifier.classify(s);
    }

    /**
     * Returns the revision string.
     *
     * @return        the revision
     */
    public String getRevision() {
        return RevisionUtils.extract("1.0");
    }

    /**
     * Returns only the classnames and what classifier it is based on.
     *
     * @return a short description
     */
    public String toString() {
        return "Auto-generated classifier wrapper, based on weka.classifiers.trees.J48Consolidated (generated with Weka 3.8.4).\n" + this.getClass().getName() + "/WekaClassifier";
    }

    /**
     * Runs the classfier from commandline.
     *
     * @param args the commandline arguments
     */
    /*public static void main(String args[]) {
        runClassifier(new WekaWrapperJ48C(), args);
    }*/
}

class WekaClassifier {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = WekaClassifier.N53f25e57286(i);
        return p;
    }
    static double N53f25e57286(Object []i) {
        double p = Double.NaN;
        if (i[129] == null) {
            p = 0;
        } else if (((Double) i[129]).doubleValue() <= 0.503194) {
            p = WekaClassifier.N1cc3f15f287(i);
        } else if (((Double) i[129]).doubleValue() > 0.503194) {
            p = WekaClassifier.N356cc8a3313(i);
        }
        return p;
    }
    static double N1cc3f15f287(Object []i) {
        double p = Double.NaN;
        if (i[128] == null) {
            p = 2;
        } else if (((Double) i[128]).doubleValue() <= 9.773509) {
            p = WekaClassifier.N2a080853288(i);
        } else if (((Double) i[128]).doubleValue() > 9.773509) {
            p = WekaClassifier.N9a5b005306(i);
        }
        return p;
    }
    static double N2a080853288(Object []i) {
        double p = Double.NaN;
        if (i[128] == null) {
            p = 2;
        } else if (((Double) i[128]).doubleValue() <= 9.258556) {
            p = WekaClassifier.N8c8595d289(i);
        } else if (((Double) i[128]).doubleValue() > 9.258556) {
            p = WekaClassifier.Nf077ab2305(i);
        }
        return p;
    }
    static double N8c8595d289(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() <= -32.315459) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() > -32.315459) {
            p = WekaClassifier.N4909bef5290(i);
        }
        return p;
    }
    static double N4909bef5290(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= 468.343273) {
            p = WekaClassifier.N4a43e782291(i);
        } else if (((Double) i[0]).doubleValue() > 468.343273) {
            p = WekaClassifier.N145d45ed301(i);
        }
        return p;
    }
    static double N4a43e782291(Object []i) {
        double p = Double.NaN;
        if (i[128] == null) {
            p = 4;
        } else if (((Double) i[128]).doubleValue() <= 7.443285) {
            p = WekaClassifier.N20632409292(i);
        } else if (((Double) i[128]).doubleValue() > 7.443285) {
            p = WekaClassifier.N34833488297(i);
        }
        return p;
    }
    static double N20632409292(Object []i) {
        double p = Double.NaN;
        if (i[128] == null) {
            p = 3;
        } else if (((Double) i[128]).doubleValue() <= 7.380681) {
            p = WekaClassifier.N6a2b83e9293(i);
        } else if (((Double) i[128]).doubleValue() > 7.380681) {
            p = WekaClassifier.N3bfbf3f0295(i);
        }
        return p;
    }
    static double N6a2b83e9293(Object []i) {
        double p = Double.NaN;
        if (i[129] == null) {
            p = 3;
        } else if (((Double) i[129]).doubleValue() <= 0.325235) {
            p = 3;
        } else if (((Double) i[129]).doubleValue() > 0.325235) {
            p = WekaClassifier.N6e51764f294(i);
        }
        return p;
    }
    static double N6e51764f294(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= 401.057452) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() > 401.057452) {
            p = 4;
        }
        return p;
    }
    static double N3bfbf3f0295(Object []i) {
        double p = Double.NaN;
        if (i[128] == null) {
            p = 2;
        } else if (((Double) i[128]).doubleValue() <= 7.408794) {
            p = WekaClassifier.N201cbea296(i);
        } else if (((Double) i[128]).doubleValue() > 7.408794) {
            p = 4;
        }
        return p;
    }
    static double N201cbea296(Object []i) {
        double p = Double.NaN;
        if (i[128] == null) {
            p = 4;
        } else if (((Double) i[128]).doubleValue() <= 7.387792) {
            p = 4;
        } else if (((Double) i[128]).doubleValue() > 7.387792) {
            p = 2;
        }
        return p;
    }
    static double N34833488297(Object []i) {
        double p = Double.NaN;
        if (i[129] == null) {
            p = 2;
        } else if (((Double) i[129]).doubleValue() <= 0.117334) {
            p = WekaClassifier.N5185cf59298(i);
        } else if (((Double) i[129]).doubleValue() > 0.117334) {
            p = WekaClassifier.N3585d2ea299(i);
        }
        return p;
    }
    static double N5185cf59298(Object []i) {
        double p = Double.NaN;
        if (i[128] == null) {
            p = 2;
        } else if (((Double) i[128]).doubleValue() <= 7.486336) {
            p = 2;
        } else if (((Double) i[128]).doubleValue() > 7.486336) {
            p = 4;
        }
        return p;
    }
    static double N3585d2ea299(Object []i) {
        double p = Double.NaN;
        if (i[129] == null) {
            p = 3;
        } else if (((Double) i[129]).doubleValue() <= 0.197567) {
            p = WekaClassifier.N39d7112b300(i);
        } else if (((Double) i[129]).doubleValue() > 0.197567) {
            p = 2;
        }
        return p;
    }
    static double N39d7112b300(Object []i) {
        double p = Double.NaN;
        if (i[129] == null) {
            p = 3;
        } else if (((Double) i[129]).doubleValue() <= 0.158487) {
            p = 3;
        } else if (((Double) i[129]).doubleValue() > 0.158487) {
            p = 1;
        }
        return p;
    }
    static double N145d45ed301(Object []i) {
        double p = Double.NaN;
        if (i[129] == null) {
            p = 4;
        } else if (((Double) i[129]).doubleValue() <= 0.391396) {
            p = WekaClassifier.N6bddaf38302(i);
        } else if (((Double) i[129]).doubleValue() > 0.391396) {
            p = 3;
        }
        return p;
    }
    static double N6bddaf38302(Object []i) {
        double p = Double.NaN;
        if (i[128] == null) {
            p = 1;
        } else if (((Double) i[128]).doubleValue() <= 8.470644) {
            p = WekaClassifier.N70d4222b303(i);
        } else if (((Double) i[128]).doubleValue() > 8.470644) {
            p = 4;
        }
        return p;
    }
    static double N70d4222b303(Object []i) {
        double p = Double.NaN;
        if (i[128] == null) {
            p = 2;
        } else if (((Double) i[128]).doubleValue() <= 7.932079) {
            p = WekaClassifier.N710b2b53304(i);
        } else if (((Double) i[128]).doubleValue() > 7.932079) {
            p = 1;
        }
        return p;
    }
    static double N710b2b53304(Object []i) {
        double p = Double.NaN;
        if (i[128] == null) {
            p = 3;
        } else if (((Double) i[128]).doubleValue() <= 7.674418) {
            p = 3;
        } else if (((Double) i[128]).doubleValue() > 7.674418) {
            p = 2;
        }
        return p;
    }
    static double Nf077ab2305(Object []i) {
        double p = Double.NaN;
        if (i[129] == null) {
            p = 1;
        } else if (((Double) i[129]).doubleValue() <= 0.391396) {
            p = 1;
        } else if (((Double) i[129]).doubleValue() > 0.391396) {
            p = 2;
        }
        return p;
    }
    static double N9a5b005306(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 497.011139) {
            p = WekaClassifier.N58310e60307(i);
        } else if (((Double) i[0]).doubleValue() > 497.011139) {
            p = WekaClassifier.N77e91f2312(i);
        }
        return p;
    }
    static double N58310e60307(Object []i) {
        double p = Double.NaN;
        if (i[128] == null) {
            p = 0;
        } else if (((Double) i[128]).doubleValue() <= 11.117382) {
            p = WekaClassifier.N7048e786308(i);
        } else if (((Double) i[128]).doubleValue() > 11.117382) {
            p = WekaClassifier.N303d24a4311(i);
        }
        return p;
    }
    static double N7048e786308(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() <= 463.943593) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() > 463.943593) {
            p = WekaClassifier.N44aed1d7309(i);
        }
        return p;
    }
    static double N44aed1d7309(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 0;
        } else if (((Double) i[2]).doubleValue() <= 17.547304) {
            p = WekaClassifier.N60af64df310(i);
        } else if (((Double) i[2]).doubleValue() > 17.547304) {
            p = 1;
        }
        return p;
    }
    static double N60af64df310(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 0;
        } else if (((Double) i[6]).doubleValue() <= 16.660311) {
            p = 0;
        } else if (((Double) i[6]).doubleValue() > 16.660311) {
            p = 1;
        }
        return p;
    }
    static double N303d24a4311(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() <= 53.839345) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() > 53.839345) {
            p = 3;
        }
        return p;
    }
    static double N77e91f2312(Object []i) {
        double p = Double.NaN;
        if (i[128] == null) {
            p = 2;
        } else if (((Double) i[128]).doubleValue() <= 17.887915) {
            p = 2;
        } else if (((Double) i[128]).doubleValue() > 17.887915) {
            p = 1;
        }
        return p;
    }
    static double N356cc8a3313(Object []i) {
        double p = Double.NaN;
        if (i[128] == null) {
            p = 4;
        } else if (((Double) i[128]).doubleValue() <= 21.268447) {
            p = WekaClassifier.N2efecb4d314(i);
        } else if (((Double) i[128]).doubleValue() > 21.268447) {
            p = 1;
        }
        return p;
    }
    static double N2efecb4d314(Object []i) {
        double p = Double.NaN;
        if (i[129] == null) {
            p = 3;
        } else if (((Double) i[129]).doubleValue() <= 0.992193) {
            p = WekaClassifier.N77bfc232315(i);
        } else if (((Double) i[129]).doubleValue() > 0.992193) {
            p = WekaClassifier.N4a5fe239331(i);
        }
        return p;
    }
    static double N77bfc232315(Object []i) {
        double p = Double.NaN;
        if (i[128] == null) {
            p = 2;
        } else if (((Double) i[128]).doubleValue() <= 14.858982) {
            p = WekaClassifier.N43d2c36316(i);
        } else if (((Double) i[128]).doubleValue() > 14.858982) {
            p = 3;
        }
        return p;
    }
    static double N43d2c36316(Object []i) {
        double p = Double.NaN;
        if (i[129] == null) {
            p = 3;
        } else if (((Double) i[129]).doubleValue() <= 0.523529) {
            p = 3;
        } else if (((Double) i[129]).doubleValue() > 0.523529) {
            p = WekaClassifier.N57c7c5c2317(i);
        }
        return p;
    }
    static double N57c7c5c2317(Object []i) {
        double p = Double.NaN;
        if (i[129] == null) {
            p = 2;
        } else if (((Double) i[129]).doubleValue() <= 0.6545) {
            p = WekaClassifier.Nf3bc8d4318(i);
        } else if (((Double) i[129]).doubleValue() > 0.6545) {
            p = WekaClassifier.N5381d1b1322(i);
        }
        return p;
    }
    static double Nf3bc8d4318(Object []i) {
        double p = Double.NaN;
        if (i[129] == null) {
            p = 2;
        } else if (((Double) i[129]).doubleValue() <= 0.611894) {
            p = WekaClassifier.N69c3553319(i);
        } else if (((Double) i[129]).doubleValue() > 0.611894) {
            p = WekaClassifier.N22dc448321(i);
        }
        return p;
    }
    static double N69c3553319(Object []i) {
        double p = Double.NaN;
        if (i[129] == null) {
            p = 2;
        } else if (((Double) i[129]).doubleValue() <= 0.535084) {
            p = WekaClassifier.N6badf52a320(i);
        } else if (((Double) i[129]).doubleValue() > 0.535084) {
            p = 2;
        }
        return p;
    }
    static double N6badf52a320(Object []i) {
        double p = Double.NaN;
        if (i[129] == null) {
            p = 2;
        } else if (((Double) i[129]).doubleValue() <= 0.527011) {
            p = 2;
        } else if (((Double) i[129]).doubleValue() > 0.527011) {
            p = 1;
        }
        return p;
    }
    static double N22dc448321(Object []i) {
        double p = Double.NaN;
        if (i[128] == null) {
            p = 4;
        } else if (((Double) i[128]).doubleValue() <= 9.002841) {
            p = 4;
        } else if (((Double) i[128]).doubleValue() > 9.002841) {
            p = 1;
        }
        return p;
    }
    static double N5381d1b1322(Object []i) {
        double p = Double.NaN;
        if (i[129] == null) {
            p = 3;
        } else if (((Double) i[129]).doubleValue() <= 0.784697) {
            p = WekaClassifier.N3450503f323(i);
        } else if (((Double) i[129]).doubleValue() > 0.784697) {
            p = WekaClassifier.N5c337afc325(i);
        }
        return p;
    }
    static double N3450503f323(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 3;
        } else if (((Double) i[3]).doubleValue() <= 5.37932) {
            p = WekaClassifier.N3050f73a324(i);
        } else if (((Double) i[3]).doubleValue() > 5.37932) {
            p = 4;
        }
        return p;
    }
    static double N3050f73a324(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 4;
        } else if (((Double) i[1]).doubleValue() <= 9.01639) {
            p = 4;
        } else if (((Double) i[1]).doubleValue() > 9.01639) {
            p = 3;
        }
        return p;
    }
    static double N5c337afc325(Object []i) {
        double p = Double.NaN;
        if (i[129] == null) {
            p = 2;
        } else if (((Double) i[129]).doubleValue() <= 0.960386) {
            p = WekaClassifier.N1e22492326(i);
        } else if (((Double) i[129]).doubleValue() > 0.960386) {
            p = 3;
        }
        return p;
    }
    static double N1e22492326(Object []i) {
        double p = Double.NaN;
        if (i[129] == null) {
            p = 2;
        } else if (((Double) i[129]).doubleValue() <= 0.912836) {
            p = WekaClassifier.N37854d39327(i);
        } else if (((Double) i[129]).doubleValue() > 0.912836) {
            p = 4;
        }
        return p;
    }
    static double N37854d39327(Object []i) {
        double p = Double.NaN;
        if (i[129] == null) {
            p = 4;
        } else if (((Double) i[129]).doubleValue() <= 0.86429) {
            p = WekaClassifier.Ndb85d4328(i);
        } else if (((Double) i[129]).doubleValue() > 0.86429) {
            p = 2;
        }
        return p;
    }
    static double Ndb85d4328(Object []i) {
        double p = Double.NaN;
        if (i[129] == null) {
            p = 2;
        } else if (((Double) i[129]).doubleValue() <= 0.796996) {
            p = 2;
        } else if (((Double) i[129]).doubleValue() > 0.796996) {
            p = WekaClassifier.N47f8eba9329(i);
        }
        return p;
    }
    static double N47f8eba9329(Object []i) {
        double p = Double.NaN;
        if (i[65] == null) {
            p = 4;
        } else if (((Double) i[65]).doubleValue() <= 0.076058) {
            p = 4;
        } else if (((Double) i[65]).doubleValue() > 0.076058) {
            p = WekaClassifier.N57ae9170330(i);
        }
        return p;
    }
    static double N57ae9170330(Object []i) {
        double p = Double.NaN;
        if (i[129] == null) {
            p = 2;
        } else if (((Double) i[129]).doubleValue() <= 0.817163) {
            p = 2;
        } else if (((Double) i[129]).doubleValue() > 0.817163) {
            p = 4;
        }
        return p;
    }
    static double N4a5fe239331(Object []i) {
        double p = Double.NaN;
        if (i[129] == null) {
            p = 4;
        } else if (((Double) i[129]).doubleValue() <= 1.198765) {
            p = WekaClassifier.N16f62b83332(i);
        } else if (((Double) i[129]).doubleValue() > 1.198765) {
            p = 4;
        }
        return p;
    }
    static double N16f62b83332(Object []i) {
        double p = Double.NaN;
        if (i[129] == null) {
            p = 4;
        } else if (((Double) i[129]).doubleValue() <= 1.154503) {
            p = WekaClassifier.N16d33f57333(i);
        } else if (((Double) i[129]).doubleValue() > 1.154503) {
            p = 2;
        }
        return p;
    }
    static double N16d33f57333(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 4;
        } else if (((Double) i[0]).doubleValue() <= 449.951385) {
            p = 4;
        } else if (((Double) i[0]).doubleValue() > 449.951385) {
            p = WekaClassifier.N355509ec334(i);
        }
        return p;
    }
    static double N355509ec334(Object []i) {
        double p = Double.NaN;
        if (i[129] == null) {
            p = 1;
        } else if (((Double) i[129]).doubleValue() <= 1.072626) {
            p = 1;
        } else if (((Double) i[129]).doubleValue() > 1.072626) {
            p = WekaClassifier.N2e7b55335(i);
        }
        return p;
    }
    static double N2e7b55335(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 4;
        } else if (((Double) i[0]).doubleValue() <= 550.293936) {
            p = 4;
        } else if (((Double) i[0]).doubleValue() > 550.293936) {
            p = 2;
        }
        return p;
    }
}
