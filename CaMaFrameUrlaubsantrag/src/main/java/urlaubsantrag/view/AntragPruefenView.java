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
package urlaubsantrag.view;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.inject.Inject;

import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.internal.Conventions;
import com.vaadin.cdi.views.CaseWorkerInfo;
import com.vaadin.cdi.views.NavigationEvent;
import com.vaadin.cdi.views.TaskInfo;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

import cm.core.CaseModel;
import cm.core.CaseWorker;
import cm.core.data.CaseFileItem;
import cm.core.services.CaseFileService;
import cm.core.services.CaseWorkerService;
import cm.core.services.TaskService;
import cm.core.states.StageTaskTransitions;
import urlaubsantrag.service.UrlaubskontoService;
/**
 * <p>
 * Vaadin CDI view for HumanTask "Antrag prüfen" ("Consider request") 
 * </p>
 * 
 * @author André Zensen
 *
 */
@CDIView("antragPruefen")
public class AntragPruefenView extends CustomComponent implements View {

    @Inject
    private CaseWorkerService cwService;    
    @Inject
    private TaskService taskService;
    @Inject
    private CaseWorkerInfo caseWorkerInfo;
    @Inject
    private CaseFileService caseFileService;
    @Inject
    private UrlaubskontoService urlaubskontoService;
    
    @Inject
    TaskInfo taskInfo;


    @Inject
    private javax.enterprise.event.Event<NavigationEvent> navigationEvent;
    
    
    private Button btnComplete;
    private Button btnCancel;
    
    // CaseFileItem fields used
    private Label antragsteller = new Label("Antragsteller (requester): ");
    private Label von = new Label("Von (from): ");
    private Label bis = new Label("Bis (until): ");
    private Label tage = new Label("Tage (days): ");
    
    private TextArea ablehnungsgrund = new TextArea("Ablehnungsgrund (reason for rejection)");
    
	private CaseFileItem urlaubsantrag;

    @Override
    public void enter(ViewChangeEvent event) {
        
        Long caseModelRefId = taskInfo.getTask().getCaseRef().getId();
        CaseModel shallowCase = new CaseModel();
        shallowCase.setId(caseModelRefId);
        String urlaubsantragCFIName = "urlaubsantrag";
        urlaubsantrag = caseFileService.getCaseFileItem(shallowCase, urlaubsantragCFIName);
        try {
			setupFields();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Layout layout = buildAntragStellenLayout();
        setCompositionRoot(layout);
    }
    
    private void setupFields() throws ParseException {
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.GERMAN);
    	DateTimeFormatter gformatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.GERMAN);
    	 
    	String dateInString;
    	LocalDate datum;
    	String newLabel;
		
		dateInString = urlaubsantrag.getProperty("von").getValue();
		datum = LocalDate.parse(dateInString, formatter);
		newLabel = von.getValue() + datum.format(gformatter);
		von.setValue(newLabel);
		
		dateInString =  urlaubsantrag.getProperty("bis").getValue();
		datum = LocalDate.parse(dateInString, formatter);
		newLabel = bis.getValue() + datum.format(gformatter);
		bis.setValue(newLabel);
		
		newLabel = tage.getValue() + urlaubsantrag.getProperty("tage").getValue();
		tage.setValue(newLabel);
		
		ablehnungsgrund.setWordWrap(true);
		ablehnungsgrund.setWidth("400px");
		
    	CaseWorker shallow = new CaseWorker();
		Long cwId = Long.valueOf(urlaubsantrag.getProperty("antragsteller").getValue());
		shallow.setId(cwId);
    	CaseWorker cw = cwService.getCaseWorkerById(shallow);
    	newLabel = antragsteller.getValue() + cw.getLastname() + ", " + cw.getFirstname();
		antragsteller.setValue(newLabel);
		
	}

	private Layout buildAntragStellenLayout() {
    	VerticalLayout layout = new VerticalLayout();
    	Label urlaubsantragStellen = new Label("Urlaubsantrag prüfen (Consider request)");
    	Label instructions = new Label("Bitte prüfen Sie den vorliegenden Urlaubsantrag und genehmigen oder lehnen Sie diesen ab"
    			+ " (please examine the vacation request and either approve or reject it):");
//    	layout.addComponents(urlaubsantragStellen, instructions, gestelltAm, antragsteller, von, bis);
    	layout.addComponents(urlaubsantragStellen, instructions, antragsteller, von, bis, tage, ablehnungsgrund);
    	
    	btnComplete = generateCompleteButton();
    	btnCancel = generateCancelButton();		
		HorizontalLayout btnLayout = new HorizontalLayout(btnComplete, btnCancel);		
		layout.addComponent(btnLayout);
		
		HorizontalLayout navBtnLayout = new HorizontalLayout(generateBackButton(), generateTaskListButton());
		layout.addComponents(navBtnLayout);
    	return layout;
	}

	private Button generateCancelButton() {
		Button cancelButton = new Button("Antrag ablehnen (reject request)");
		cancelButton.addClickListener(e -> {
			if(ablehnungsgrund.getValue() != "" || ablehnungsgrund.getValue() != null) {
				urlaubsantrag.getProperty("genehmigt").setValue(String.valueOf(false));
				urlaubsantrag.getProperty("required").setValue(String.valueOf(false));
				urlaubsantrag.getProperty("ablehnungsgrund").setValue(ablehnungsgrund.getValue());
				caseFileService.updateCaseFileItem(urlaubsantrag);
				taskService.transitionTask(taskInfo.getTask(), caseWorkerInfo.getUser(), StageTaskTransitions.complete);
				btnComplete.setEnabled(false);
				btnCancel.setEnabled(false);
			} else {
				Notification.show("Bitte geben Sie einen Ablehnungsgrund ein. (Please enter a reason for rejecting the request.)");
			}
		});
		return cancelButton;
	}

	private Button generateCompleteButton() {
		Button completeButton = new Button("Antrag genehmigen (approve request)");
		completeButton.addClickListener(e -> {
			urlaubsantrag.getProperty("genehmigt").setValue(String.valueOf(true));	

			caseFileService.updateCaseFileItem(urlaubsantrag);
			taskService.transitionTask(taskInfo.getTask(), caseWorkerInfo.getUser(), StageTaskTransitions.complete);
			btnComplete.setEnabled(false);
			btnCancel.setEnabled(false);
		});
		return completeButton;
	}

	private Button generateTaskListButton() {
		Button taskListButton = new Button("Task List");
		taskListButton.addClickListener(e -> {
			navigationEvent.fire(new NavigationEvent("task-list"));
		});
		return taskListButton;
	}

	private Button generateBackButton() {
        Button button = new Button("Back");
        button.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                navigationEvent.fire(new NavigationEvent(Conventions
                        .deriveMappingForView(AntragPruefenView.class)));
            }
        });
        return button;
    }
}