package org.chusnaval.etg;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.chusnaval.FileFinder;
import org.chusnaval.GeneratorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class EntitiesTestsGeneratorApplication implements CommandLineRunner {

	private static final String CLASS_FILE = "class";

	private static final String DIRECTORY = "dir";

	private static final String OUTPUT_FOLDER = "output";

	private static final String OUTPUT_PACKAGE = "package";

	private static final String RECURSIVE = "r";

	private static final Logger log = LoggerFactory.getLogger(EntitiesTestsGeneratorApplication.class);

	public static void main(String[] args) {

		new SpringApplicationBuilder(EntitiesTestsGeneratorApplication.class).web(WebApplicationType.NONE).run(args);
	}

	@Override
	public void run(String... args) {
		// create the command line parser
		CommandLineParser parser = new DefaultParser();

		// create Options object
		Options options = new Options();

		options.addOption(Option.builder(DIRECTORY).hasArg()
				.desc("the dir where it will be used, this or class option is mandatory").build());

		options.addOption(Option.builder(CLASS_FILE).hasArg()
				.desc("the class where it will be used, this or package option is mandatory").build());

		options.addOption(Option.builder(OUTPUT_PACKAGE).hasArg()
				.desc("the output package where test files will be generated").build());

		options.addOption(Option.builder(OUTPUT_FOLDER).hasArg()
				.desc("the output folder where test files will be generated").build());

		options.addOption(Option.builder(RECURSIVE).hasArg(false).desc("Get recursive execution").build());

		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			// validate that package or class has been set
			if (isValidInputsInCommandLine(line)) {
				EntitiesTestGenerator generator = new EntitiesTestGenerator(line.hasOption(RECURSIVE), new FileFinder("glob:*.java"));
				String outputFolder = line.getOptionValue(OUTPUT_FOLDER);
				String outputPackage = line.getOptionValue(OUTPUT_PACKAGE);

				if (line.hasOption(DIRECTORY)) {
					generator.generateTestFiles(DIRECTORY, line.getOptionValue(DIRECTORY), outputFolder, outputPackage);
				} else if (line.hasOption(CLASS_FILE)) {
					generator.generateTestFiles(CLASS_FILE, line.getOptionValue(CLASS_FILE), outputFolder, outputPackage);
				}
			} else {
				printHelp(options);
			}

		} catch (ParseException | IOException | GeneratorException exp) {
			log.error("Unexpected exception:" + exp.getMessage(), exp);
		}
	}

	/**
	 * This app needs a directory or class file to parse
	 *
	 * @param line command line received
	 * @return logical value that validate input
	 */
	private boolean isValidInputsInCommandLine(CommandLine line) {
		return (line.hasOption(DIRECTORY) || line.hasOption(CLASS_FILE));
	}

	/**
	 * Prints the usage help
	 *
	 * @param options options to print
	 */
	private void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("help", options);
	}
}
