/*
 * Copyright © 2018-2019 André Zensen, University of Applied Sciences Bielefeld
 * and various authors (see https://www.fh-bielefeld.de/wug/forschung/ag-pm)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cm.core.utils;

import java.util.ArrayList;
import java.util.List;

import cm.core.CaseModel;
import cm.core.CaseRole;
import cm.core.Milestone;
import cm.core.Stage;
import cm.core.data.CaseFileItem;
import cm.core.data.MultiplicityEnum;
import cm.core.data.SimpleProperty;
import cm.core.listeners.EventListener;
import cm.core.rules.RepetitionRule;
import cm.core.rules.RequiredRule;
import cm.core.sentries.CaseFileItemOnPart;
import cm.core.sentries.ElementOnPart;
import cm.core.sentries.EntrySentry;
import cm.core.sentries.ExitSentry;
import cm.core.sentries.IfPart;
import cm.core.services.commands.stagetask.TaskTransitionCommand;
import cm.core.states.CaseFileItemTransition;
import cm.core.states.EventMilestoneTransitions;
import cm.core.states.StageTaskTransitions;
import cm.core.tasks.CaseTask;
import cm.core.tasks.HumanTask;
import cm.core.tasks.ProcessTask;

/**
 * <p>Central factory class with methods providing {@link CaseModel} blueprints to
 * be persisted. Can be used to store and reference CaseRoles as enumerations.</p>
 * 
 * @author André Zensen
 *
 */
public class CaseFactory {

	public enum CaseRoles {
		bearbeiter, pruefer
	}

	public enum CaseModelNames {
		Urlaubsantrag
	}
	
	public enum SubCaseModelNames {
		
	}

	public static List<String> getCaseModelNames() {
		ArrayList<String> caseModelNames = new ArrayList<>();
		for (CaseModelNames name : CaseModelNames.values()) {
			caseModelNames.add(name.toString());
		}
		return caseModelNames;
	}
	
	public static List<String> getInstantiableCaseModelNames() {
		ArrayList<String> caseModelNames = new ArrayList<>();
		for (CaseModelNames name : CaseModelNames.values()) {
			caseModelNames.add(name.toString());
		}
		return caseModelNames;
	}

	public static CaseModel getCaseModelByName(String caseName) {
		if (caseName.equals(CaseModelNames.Urlaubsantrag.toString())) {
			CaseModel urlaubsantrag = getUrlaubsantragCaseModel();
			return urlaubsantrag;
		}
		return null;
	}

	public static List<CaseRole> getRolesUsed() {
		ArrayList<CaseRole> caseRoles = new ArrayList<>();
		for (CaseRoles role : CaseRoles.values()) {
			CaseRole newRole = new CaseRole(role.toString());
			caseRoles.add(newRole);
		}
		return caseRoles;
	}

	public static CaseModel getUrlaubsantragCaseModel() {
		CaseModel model = new CaseModel("urlaubsantrag", "Urlaubsantrag");
		model.setAutoComplete(true);		
		CaseFileItem urlaubsantrag = new CaseFileItem("urlaubsantrag",
				MultiplicityEnum.ExactlyOne.toString(), "Urlaubsantrag");
		model.getCaseFile().addCaseFileItem(urlaubsantrag);
	
		HumanTask antragsdatenErfassen = new HumanTask("antragsdatenErfassen",
				"Daten für Urlaubsantrag erfassen", model);
		antragsdatenErfassen.setCaseRole(new CaseRole("bearbeiter"));
		HumanTask antragPruefen = new HumanTask("antragPruefen",
				"Urlaubsantrag prüfen", model);
		antragPruefen.setCaseRole(new CaseRole("pruefer"));
		ProcessTask antragstellerInformieren =
				new ProcessTask("antragstellerInformieren",
						"Antragsteller Informieren", model);
		ProcessTask urlaubskontoAktualisieren =
				new ProcessTask("urlaubskontoAktualisieren",
						"Urlaubskonto aktualisieren", model);	
		RequiredRule aktualisierenRequired =
				new RequiredRule("required", urlaubsantrag);
		urlaubskontoAktualisieren.setRequiredRule(aktualisierenRequired);
		
		EntrySentry enterAntragPruefen = new EntrySentry("", "", antragPruefen);
		ElementOnPart enterAntragPruefenOn =
				new ElementOnPart(enterAntragPruefen, antragsdatenErfassen,
						StageTaskTransitions.complete.toString());		
		EntrySentry enterAntragstellerInformieren =
				new EntrySentry("", "", antragstellerInformieren);
		ElementOnPart enterAntragstellerInformierenOn =
				new ElementOnPart(enterAntragstellerInformieren, antragPruefen,
						StageTaskTransitions.complete.toString());		
		EntrySentry enterUrlaubskontoAktualisieren =
				new EntrySentry("", "", urlaubskontoAktualisieren);
		ElementOnPart enterUrlaubskontoAktualisierenOn =
				new ElementOnPart(enterUrlaubskontoAktualisieren,
						antragPruefen, StageTaskTransitions.complete.toString());
		IfPart urlaubskontoAktualisierenIf = new IfPart("kontoAktualisieren",
				enterUrlaubskontoAktualisieren, urlaubsantrag);
		
		SimpleProperty antragsteller = new SimpleProperty("antragsteller", "");
		SimpleProperty von = new SimpleProperty("von", "");
		SimpleProperty bis = new SimpleProperty("bis", "");
		SimpleProperty tage = new SimpleProperty("tage", "");
		SimpleProperty genehmigt = new SimpleProperty("genehmigt", "false");
		SimpleProperty required = new SimpleProperty("required", "true");
		SimpleProperty ablehnungsgrund = new SimpleProperty("ablehnungsgrund", "");
		SimpleProperty pruefer = new SimpleProperty("pruefer", "");
		urlaubsantrag.addProperty(antragsteller, von, bis, tage, genehmigt,
				required, ablehnungsgrund, pruefer);	
		
		model.getContextState().create();
		return model;
	}
}
