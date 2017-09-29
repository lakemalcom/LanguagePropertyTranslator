package com.peterfranza.propertytranslator;

import com.peterfranza.propertytranslator.translators.LOREMTranslator;
import com.peterfranza.propertytranslator.translators.Translator;
import com.peterfranza.propertytranslator.translators.DictionaryTranslator;

public enum TranslatorGeneratorType {
	LOREM(new LOREMTranslator()), DICTIONARY(new DictionaryTranslator());
	
	private Translator translator;

	TranslatorGeneratorType(Translator clazz) {
		this.translator = clazz;
	}
	
	public Translator getTranslator() {
		return translator;
	}
}
