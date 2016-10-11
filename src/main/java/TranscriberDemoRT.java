/*
 * Copyright 1999-2013 Carnegie Mellon University.
 * Portions Copyright 2004 Sun Microsystems, Inc.
 * Portions Copyright 2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */


import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.decoder.adaptation.Stats;
import edu.cmu.sphinx.decoder.adaptation.Transform;
import edu.cmu.sphinx.result.WordResult;

import java.io.InputStream;

/**
 * A simple example that shows how to transcribe a continuous audio file that
 * has multiple utterances in it.
 */
public class TranscriberDemoRT {

    public static void main(String[] args) throws Exception {
        System.out.println("Loading models...");

        Configuration configuration = new Configuration();

        // Load model from the jar
        configuration
                .setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");

        // You can also load model from folder
        // configuration.setAcousticModelPath("file:en-us");

        configuration
                .setDictionaryPath("5186.dic");
        configuration
                .setLanguageModelPath("5186.lm");

        StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(
                configuration);
        InputStream stream = TranscriberDemoRT.class
                .getResourceAsStream("ginGameOct10.wav");
        // stream.skip(44);

        // Simple recognition with generic model
        recognizer.startRecognition(stream);
        SpeechResult result;
        while ((result = recognizer.getResult()) != null) {

            System.out.format("Hypothesis: %s\n", result.getHypothesis());

            System.out.println("List of recognized words and their times:");
            for (WordResult r : result.getWords()) {
                System.out.println(r);
            }

            System.out.println("Best 3 hypothesis:");
            for (String s : result.getNbest(3))
                System.out.println(s);

        }
        recognizer.stopRecognition();
        System.out.println("start with mic ... ");
        LiveSpeechRecognizer recognizer2 = new LiveSpeechRecognizer(configuration);
// Start recognition process pruning previously cached data.
        recognizer2.startRecognition(true);
        SpeechResult result2 = recognizer2.getResult();
// Pause recognition process. It can be resumed then with startRecognition(false).
        while ((result2 = recognizer2.getResult()) != null) {

            System.out.format("Hypothesis: %s\n", result2.getHypothesis());

            System.out.println("List of recognized words and their times:");
            for (WordResult r : result2.getWords()) {
                System.out.println(r);
            }

            System.out.println("Best 3 hypothesis:");
            for (String s : result2.getNbest(3))
                System.out.println(s);
            //recognizer2.startRecognition(false);
            break;
        }
        recognizer2.stopRecognition();

    }
}