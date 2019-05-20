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
package urlaubsantrag.impl.processtask;

import java.util.logging.Logger;

import javax.enterprise.inject.spi.CDI;

import cm.core.CaseModel;
import cm.core.data.CaseFileItem;
import cm.core.services.CaseFileService;
import cm.core.tasks.ProcessTaskImplementation;
import cm.core.tasks.ProcessTask;
/**
 * Implementation for ProcessTask "Antragsteller informieren" ("Notify requester").
 * @author André Zensen
 *
 */
public class AntragstellerInformierenProcess extends ProcessTaskImplementation {

	
	public AntragstellerInformierenProcess(ProcessTask processTask) {
		super(processTask);
	}

	@Override
	public void startProcess() {
		// get CaseFileService via CDI-context, since @Inject does not work in a non-managed bean
		CaseFileService cfService = CDI.current().select(CaseFileService.class).get();		
		Long caseModelRefId = this.processTask.getCaseRef().getId();
        CaseModel shallowCase = new CaseModel();
        shallowCase.setId(caseModelRefId);
        CaseFileItem urlaubsantrag = cfService.getCaseFileItem(shallowCase, "urlaubsantrag");
        boolean genehmigt = Boolean.parseBoolean(urlaubsantrag.getProperty("genehmigt").getValue());
        if(genehmigt) {
        	// Antragsteller per E-Mail informieren / notify requester
        	Logger logger = Logger.getLogger(this.getClass().getSimpleName());
        	logger.info("Der Antragsteller mit der ID " + urlaubsantrag.getProperty("antragsteller").getValue()
        			+ " wurde über seinen genehmigten Urlaub informiert.");
        	logger.info("The requester with the ID " + urlaubsantrag.getProperty("antragsteller").getValue()
        			+ " has been notified.");
        } else {
        	String grund = urlaubsantrag.getProperty("ablehnungsgrund").getValue();
        	// Antragsteller mit Grund der Ablehnung informieren / notify requester with reason for rejection
        	Logger logger = Logger.getLogger(this.getClass().getSimpleName());
        	logger.info("Der Antragsteller mit der ID " + urlaubsantrag.getProperty("antragsteller").getValue()
        			+ " wurde über seinen abgelehnten Urlaub informiert. Der Ablehnungsgrund lautet:"
        			+ "\n"
        			+ grund);
        	logger.info("The requester with the ID " + urlaubsantrag.getProperty("antragsteller").getValue()
        			+ " has been notified. Reason for rejection:"
        			+ "\n"
        			+ grund);
        }        
        this.processTask.getContextState().complete();
	}

	@Override
	public void executeCallBack() {
		// no callback required		
	}

}
