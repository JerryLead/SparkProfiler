package util;

import com.tagtraum.perf.gcviewer.GCViewerArgsParser;
import com.tagtraum.perf.gcviewer.GCViewerArgsParserException;
import com.tagtraum.perf.gcviewer.exp.DataWriter;
import com.tagtraum.perf.gcviewer.exp.DataWriterType;
import com.tagtraum.perf.gcviewer.exp.impl.DataWriterFactory;
import com.tagtraum.perf.gcviewer.imp.DataReaderException;
import com.tagtraum.perf.gcviewer.imp.DataReaderFacade;
import com.tagtraum.perf.gcviewer.model.GCModel;
import com.tagtraum.perf.gcviewer.model.GCResource;
import com.tagtraum.perf.gcviewer.view.SimpleChartRenderer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GCViewerNoneGUI {
    private static final Logger LOGGER = Logger.getLogger(GCViewerNoneGUI.class.getName());
    private static final int EXIT_OK = 0;
    private static final int EXIT_EXPORT_FAILED = -1;
    private static final int EXIT_ARGS_PARSE_FAILED = -2;

    private GCViewerArgsParser gcViewerArgsParser;

    public GCViewerNoneGUI() {
        this.gcViewerArgsParser = new GCViewerArgsParser();
    }

    public static void main(String[] args) throws InvocationTargetException, InterruptedException {
        (new GCViewerNoneGUI()).doMain(args);
    }

    public void doMain(String[] args) throws InvocationTargetException, InterruptedException {
        GCViewerArgsParser argsParser = this.gcViewerArgsParser;

        try {
            argsParser.parseArguments(args);
        } catch (GCViewerArgsParserException var9) {
            usage();
            LOGGER.log(Level.SEVERE, var9.getMessage(), var9);
            System.exit(-2);
        }

        if(argsParser.getArgumentCount() > 3) {
            usage();
        } else if(argsParser.getArgumentCount() >= 2) {
            LOGGER.info("GCViewer command line mode");
            GCResource gcResource = argsParser.getGcResource();
            String summaryFilePath = argsParser.getSummaryFilePath();
            String chartFilePath = argsParser.getChartFilePath();
            DataWriterType type = argsParser.getType();

            try {
                this.export(gcResource, summaryFilePath, chartFilePath, type);
                LOGGER.info("export completed successfully");
                // System.exit(0);
            } catch (Exception var8) {
                LOGGER.log(Level.SEVERE, "Error during report generation", var8);
                System.exit(-1);
            }
        }

    }

    private void export(GCResource gcResource, String summaryFilePath, String chartFilePath, DataWriterType type) throws IOException, DataReaderException {
        DataReaderFacade dataReaderFacade = new DataReaderFacade();
        GCModel model = dataReaderFacade.loadModel(gcResource);
        try {
            this.exportType(model, summaryFilePath, type);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        if(chartFilePath != null) {
            this.renderChart(model, chartFilePath);
        }

    }

    private void exportType(GCModel model, String summaryFilePath, DataWriterType type) throws Throwable {
        DataWriter summaryWriter = DataWriterFactory.getDataWriter(new File(summaryFilePath), type);
        Throwable var5 = null;

        try {
            summaryWriter.write(model);
        } catch (Throwable var14) {
            var5 = var14;
            throw var14;
        } finally {
            if(summaryWriter != null) {
                if(var5 != null) {
                    try {
                        summaryWriter.close();
                    } catch (Throwable var13) {
                        var13.printStackTrace();
                    }
                } else {
                    summaryWriter.close();
                }
            }

        }

    }

    private void renderChart(GCModel model, String chartFilePath) throws IOException {
        SimpleChartRenderer renderer = new SimpleChartRenderer();
        renderer.render(model, new FileOutputStream(new File(chartFilePath)));
    }

    private static void usage() {
        System.out.println("Welcome to GCViewer with cmdline");
        System.out.println("java -jar gcviewer.jar [<gc-log-file|url>] -> opens gui and loads given file");
        System.out.println("java -jar gcviewer.jar [<gc-log-file|url>];[<gc-log-file|url>];[...] -> opens gui and loads given files as series of rotated logfiles");
        System.out.println("java -jar gcviewer.jar [<gc-log-file>] [<export.csv>] -> cmdline: writes report to <export.csv>");
        System.out.println("java -jar gcviewer.jar [<gc-log-file|url>];[<gc-log-file|url>];[...] [<export.csv>] -> cmdline: loads given files as series of rotated logfiles and writes report to <export.csv>");
        System.out.println("java -jar gcviewer.jar [<gc-log-file>] [<export.csv>] [<chart.png>] -> cmdline: writes report to <export.csv> and renders gc chart to <chart.png>");
        System.out.println("java -jar gcviewer.jar [<gc-log-file|url>];[<gc-log-file|url>];[...] [<export.csv>] [<chart.png>] -> cmdline: loads given files as series of rotated logfiles and writes report to <export.csv> and renders gc chart to <chart.png>");
        System.out.println("java -jar gcviewer.jar [<gc-log-file|url>] [<export.csv>] [<chart.png>] [-t <SUMMARY, CSV, CSV_TS, PLAIN, SIMPLE>]");
        System.out.println("java -jar gcviewer.jar [<gc-log-file|url>];[<gc-log-file|url>];[...] [<export.csv>] [<chart.png>] [-t <SUMMARY, CSV, CSV_TS, PLAIN, SIMPLE>]");
    }
}
