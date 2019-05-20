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
import urlaubsantrag.model.Urlaubskonto;
import urlaubsantrag.service.UrlaubskontoService;
import cm.core.tasks.ProcessTask;
/**
 * Implementation of ProcessTask "Urlaubskonto aktualisieren" ("Update holiday account").
 * @author André Zensen
 *
 */
public class UrlaubskontoAktualisierenProcess extends ProcessTaskImplementation {

	private CaseFileItem urlaubsantrag;
	
	public UrlaubskontoAktualisierenProcess(ProcessTask processTask) {
		super(processTask);
	}

	@Override
	public void startProcess() {
		// get CaseFileService via CDI-context, since @Inject does not work in a non-managed bean
		CaseFileService cfService = CDI.current().select(CaseFileService.class).get(); 
		UrlaubskontoService urlaubskontoService = CDI.current().select(UrlaubskontoService.class).get();
		
		Long caseModelRefId = this.processTask.getCaseRef().getId();
        CaseModel shallowCase = new CaseModel();
        shallowCase.setId(caseModelRefId);
        String urlaubsantragCFIName = "urlaubsantrag";
//        urlaubsantrag = caseFileService.getCaseFileItem(shallowCase, urlaubsantragCFIName);
        urlaubsantrag = cfService.getCaseFileItem(shallowCase, urlaubsantragCFIName);
		long antragstellerId = Long.parseLong(urlaubsantrag.getProperty("antragsteller").getValue());
		int tage = Integer.parseInt(urlaubsantrag.getProperty("tage").getValue());
		urlaubskontoService.substractTage(antragstellerId, tage);
		Urlaubskonto konto = urlaubskontoService.getUrlaubskonto(antragstellerId);
        
        if(urlaubsantrag != null) {
        	Logger logger = Logger.getLogger(this.getClass().getSimpleName());
        	logger.info("Der Urlaubsantrag"
        			+ "von Antragsteller mit der ID " + urlaubsantrag.getProperty("antragsteller").getValue()
        			+ "\n"
        			+ " vom " + urlaubsantrag.getProperty("von").getValue()
        			+ " bis zum " + urlaubsantrag.getProperty("bis").getValue()
        			+ "\n"
        			+ "wurde genehmigt. Es verbleiben " + konto.getUrlaubstage() + " Tage.");
        	logger.info("The vacation request"
        			+ " by requester with ID " + urlaubsantrag.getProperty("antragsteller").getValue()
        			+ "\n"
        			+ " from " + urlaubsantrag.getProperty("von").getValue()
        			+ " until " + urlaubsantrag.getProperty("bis").getValue()
        			+ "\n"
        			+ "was approved. " + konto.getUrlaubstage() + " days remain in the account.");
        }
        this.processTask.getContextState().complete();
	}

	@Override
	public void executeCallBack() {
		// no call back required		
	}

}
