package org.aksw.owl2nl;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Optimizer {

    public String Optimise(String text) {
        String optimisedText;
        try {
            StanfordCoreNLP stanfordCoreNLP = Pipeline.getPipeline();
            if (text == null || text == "")
                return text;// = "something that plays jazz and something that plays karyoke";
            optimisedText = text;
            CoreDocument coreDocument = new CoreDocument(text);

            stanfordCoreNLP.annotate(coreDocument);
            List<CoreLabel> coreLabelList = coreDocument.tokens();
            List<String> posList = new ArrayList<>();
            List<Dict> list = new ArrayList<Dict>();

            for (CoreLabel coreLabel : coreLabelList) {
                //System.out.println(coreLabel.originalText());
                String pos = coreLabel.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                //System.out.println(coreLabel.originalText() + "=" + pos);
                posList.add(pos);
                list.add(new Dict(pos, coreLabel.originalText()));
            }

            List<Integer> ccList = new ArrayList();
            List<Integer> commaList = new ArrayList();
            List<Integer> combinedCcCommaList = new ArrayList();
            List<Dict> verbList = new ArrayList();

            List<String> finalText = new ArrayList();

            for (int i = 0; i < list.size(); i++) {
                if ((list.get(i)).Key.equals("VBZ")) {
                    verbList.add(new Dict(i, list.get(i).Value));
                } else if ((list.get(i)).Key.equals("CC")) {
                    ccList.add(i);
                } else if ((list.get(i)).Value.equals(",")) {
                    commaList.add(i);
                    //System.out.println("comma is found");
                }
            }
            boolean sameVerb = false;
            /*if(ccList.size()!=1)
                return text;
            if(verbList.size()!=2) return text;

            if (verbList.get(0).Value.equals(verbList.get(1).Value)) {

                for (int i = 0; i <= ccList.get(0); i++) {
                    finalText.add(list.get(i).Value);
                }
                for (int i = Integer.parseInt(verbList.get(1).Key) + 1; i < list.size(); i++) {
                    finalText.add(list.get(i).Value);
                }
            } */
            //for one cc and two verb
            if (ccList.size() == 1 && verbList.size() == 2) {

                if (verbList.get(0).Value.equals(verbList.get(1).Value)) {
                    sameVerb = true;
                    for (int i = 0; i <= ccList.get(0); i++) {
                        finalText.add(list.get(i).Value);
                    }
                    for (int i = Integer.parseInt(verbList.get(1).Key) + 1; i < list.size(); i++) {
                        finalText.add(list.get(i).Value);
                    }
                }
            }
            /*for one cc one comma and  more verbs*/
            else if (ccList.size() == 1 && verbList.size() == 3 && commaList.size() == 1) {
                // System.out.println("entering the correct if else");
                for (int j = 1; j < verbList.size(); j++) {
                    if (!(verbList.get(0).Value.equals(verbList.get(j).Value))) {
                        //not same verb
                        sameVerb = false;
                        //return text;

                    }
                    sameVerb = true;

                }
                //System.out.println("same verb found");
                //if (sameVerb) {
                    // sameVerb=true;
                    if (commaList.get(0) < ccList.get(0)) {
                        abc((List<Dict>) list, ccList, commaList, (List<Dict>) verbList, finalText);
                    } else {
                        /*implement when comma comes after cc*/
                        abc((List<Dict>) list, commaList, ccList, (List<Dict>) verbList, finalText);
                    }
                //}

            }
            /*connecting comma and cc and trying to generalize*/
            else if (ccList.size() >= 1 && verbList.size() >= 2 && commaList.size() >= 1) {
                int loopSize = 0;
                int ccListPointer = 0;
                int commaListPointer = 0;
                loopSize = ccList.size() + commaList.size();
                //determining loop size. this logic is not correct. correct it by summing both size.
                //if(commaList.size()>0){
                //if (commaList.size()>ccList.size()){
                //    loopSize=commaList.size();
                //}
                //else{
                //   loopSize=ccList.size();
                // }

                for (int i = 0; i < loopSize; i++) {
                    if (ccListPointer < ccList.size() && commaListPointer < commaList.size()) {
                        if (commaList.get(commaListPointer) < ccList.get(ccListPointer)) {
                            combinedCcCommaList.add(commaList.get(commaListPointer));
                            commaListPointer++;
                        } else {
                            combinedCcCommaList.add(ccList.get(ccListPointer));
                            ccListPointer++;
                        }
                    } else if (ccListPointer >= ccList.size()) {
                        combinedCcCommaList.add(commaList.get(commaListPointer));
                        commaListPointer++;
                    } else if (commaListPointer >= commaList.size()) {
                        combinedCcCommaList.add(ccList.get(ccListPointer));
                        ccListPointer++;
                    }
                }
                /*check whether the verbs are same or not*/
                for (int j = 1; j < verbList.size(); j++) {
                    if (!(verbList.get(0).Value.equals(verbList.get(j).Value))) {
                        //not same verb
                        sameVerb = false;
                        //return text;

                    }
                    sameVerb = true;

                }
                /*join*/
                if (sameVerb && verbList.size() == (1 + combinedCcCommaList.size())) {
                    //int startAdding;
                    //System.out.println("loopszie="+loopSize);
                    for (int i = 0; i <= combinedCcCommaList.get(0); i++) {
                        finalText.add(list.get(i).Value);
                    }
                    for (int j = 1; j < loopSize + 1; j++) {
                        // if (j==0) {

                        //}
                        if (j == loopSize) {
                            for (int i = Integer.parseInt(verbList.get(j).Key) + 1; i < list.size(); i++) {
                                finalText.add(list.get(i).Value);
                            }
                        } else {
                            for (int i = Integer.parseInt(verbList.get(j).Key) + 1; i <= combinedCcCommaList.get(j); i++) {
                                finalText.add(list.get(i).Value);
                            }
                        }

                    }
                }

                //}

            } else {
                // return text;
            }
             if (sameVerb) {
                 optimisedText = String.join(" ", finalText);
                 sameVerb = false;
             }
            return optimisedText;
        } catch (Exception ex) {
            return "error:" + ex.getMessage();
        }
    }

    private void abc(List<Dict> list, List<Integer> ccList, List<Integer> commaList, List<Dict> verbList, List<String> finalText) {
        for (int i = 0; i <= commaList.get(0); i++) {
            finalText.add(list.get(i).Value);
        }
        for (int i = Integer.parseInt(verbList.get(1).Key) + 1; i <= ccList.get(0); i++) {
            finalText.add(list.get(i).Value);
        }
        for (int i = Integer.parseInt(verbList.get(2).Key) + 1; i < list.size(); i++) {
            finalText.add(list.get(i).Value);
        }
    }
}
class Pipeline {
    private static Properties properties;
    private static String propertiesName="tokenize, ssplit, pos";
    private static StanfordCoreNLP stanfordCoreNLP;

    private Pipeline(){

    }
    static {
        properties=new Properties();
        properties.setProperty("annotators",propertiesName);
    }
    public static StanfordCoreNLP getPipeline(){
        if(stanfordCoreNLP==null){
            stanfordCoreNLP=new StanfordCoreNLP(properties);

        }
        return stanfordCoreNLP;

    }
}

class Dict {
    public String Key;

    public String Value;

    public Dict(String key, String value) {
        Key = key;
        Value = value;
    }

    public Dict(int index, String value) {
        Key = String.valueOf(index);
        Value = value;
    }
}
