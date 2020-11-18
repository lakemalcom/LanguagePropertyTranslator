package com.peterfranza.propertytranslator.translators;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.maven.shared.model.fileset.util.FileSetManager;

import com.peterfranza.propertytranslator.OnErrorKey;
import com.peterfranza.propertytranslator.OnMissingKey;
import com.peterfranza.propertytranslator.TranslationInputValidator;
import com.peterfranza.propertytranslator.TranslationPropertyFileReader;
import com.peterfranza.propertytranslator.TranslatorDictionaryEvolutionConfiguration;
import com.peterfranza.propertytranslator.translators.JSONDictionaryTranslator.TranslationObject;

public class JSONDictionaryEvolutionProcessor {

	private static final class EvolutionFile implements Comparable<EvolutionFile> {
		
		private String filename;
		private TranslatorDictionaryEvolutionConfiguration config;
		
		
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((filename == null) ? 0 : filename.hashCode());
			return result;
		}



		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			EvolutionFile other = (EvolutionFile) obj;
			if (filename == null) {
				if (other.filename != null)
					return false;
			} else if (!filename.equals(other.filename))
				return false;
			return true;
		}



		@Override
		public int compareTo(EvolutionFile o) {
			return filename.compareTo(o.filename);
		}
		
		private File getAsFile() {
			File root = new File(config.fileset.getDirectory());
			return new File(root, filename);
		}
		
	}
	
	public static void process(List<TranslatorDictionaryEvolutionConfiguration> configs,
			Function<String, Optional<String>> sourcePhraseLookup, Consumer<TranslationObject> consumer,
			Consumer<String> infoLogConsumer, Consumer<String> errorLogConsumer) throws IOException {
		
		Set<EvolutionFile> files = new TreeSet<EvolutionFile>();
		
		for(TranslatorDictionaryEvolutionConfiguration c: configs) {
			for(String f: Arrays.asList( new FileSetManager().getIncludedFiles(c.fileset))) {
				EvolutionFile ef = new EvolutionFile();
				ef.config = c;
				ef.filename = f;
				files.add(ef);
			}
		}
		
		
		for(EvolutionFile f: files) {
			infoLogConsumer.accept("Evolving '" + f.getAsFile().getAbsolutePath() + "' " + f.config);
			TranslationPropertyFileReader.read(f.getAsFile(), f.config.delimiter, (e) -> {
				String key = e.getKey();
				String value = e.getValue();

				TranslationObject obj = new TranslationObject();
				obj.sourcePhrase = sourcePhraseLookup.apply(key).orElse(null);
				obj.calculatedKey = key;
				obj.targetPhrase = value;
				obj.type = f.config.translationType;

//				if (f.config.missingKey == OnMissingKey.SKIP && obj.sourcePhrase != null) {
//					return;
//				}

//				Optional<String> sourcePhrase = sourcePhraseLookup.apply(key);
//				if (sourcePhrase.isPresent()) {
//					boolean valid = TranslationInputValidator.checkValidity(key, sourcePhrase.get(), value,
//							errorLogConsumer);
//					if (!valid && f.config.errorKey == OnErrorKey.SKIP) {
//						errorLogConsumer.accept("Errors found skipping key " + key);
//						return;
//					}	
//				}
				
				consumer.accept(obj);
			});
		}
			
		
	}

}
