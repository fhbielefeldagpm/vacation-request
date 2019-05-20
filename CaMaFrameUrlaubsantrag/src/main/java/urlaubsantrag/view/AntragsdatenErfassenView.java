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

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.internal.Conventions;
import com.vaadin.cdi.views.CaseWorkerInfo;
import com.vaadin.cdi.views.NavigationEvent;
import com.vaadin.cdi.views.TaskInfo;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import cm.core.CaseModel;
import cm.core.CaseWorker;
import cm.core.data.CaseFileItem;
import cm.core.services.CaseFileService;
import cm.core.services.CaseWorkerService;
import cm.core.services.TaskService;
import cm.core.states.StageTaskTransitions;
import cm.core.utils.CaseFactory;
/**
 * <p>
 * Vaadin CDI view for HumanTask "Antragsdaten erfassen" ("Enter request data") 
 * </p>
 * 
 * @author André Zensen
 *
 */
@CDIView("antragsdatenErfassen")
public class AntragsdatenErfassenView extends CustomComponent implements View {

    @Inject
    private CaseWorkerService cwService;    
    @Inject
    private TaskService taskService;
    @Inject
    private CaseWorkerInfo caseWorkerInfo;
    @Inject
    private CaseFileService caseFileService;
    
    @Inject
    TaskInfo taskInfo;


    @Inject
    private javax.enterprise.event.Event<NavigationEvent> navigationEvent;
    
    
    private Button btnComplete;
    private Button btnCancel;
    
    // CaseFileItem fields
//	Property gestelltAm = new PropertyDate("gestelltAm", 0, null, now);
//	Property gestelltVon = new PropertyLong("antragsteller", 0, null, cw.getId());
//	Property von = new PropertyDate("von", 0, null, null);
//	Property bis = new PropertyDate("bis", 0, null, null);
//	Property genehmigt = new PropertyBoolean("genehmigt", 0, null, false);
    private ComboBox<CaseWorker> comboWorkers = new ComboBox<>();
    private DateField von = new DateField();
    private DateField bis = new DateField();
    private TextField tage = new TextField("Tage");
	private Long antragstellerId;
	private CaseFileItem urlaubsantrag;
	
    @Override
    public void enter(ViewChangeEvent event) {
        Layout layout = buildAntragStellenLayout();
        setCompositionRoot(layout);
        
        Long caseModelRefId = taskInfo.getTask().getCaseRef().getId();
        CaseModel shallowCase = new CaseModel();
        shallowCase.setId(caseModelRefId);
        String urlaubsantragCFIName = "urlaubsantrag";
        urlaubsantrag = caseFileService.getCaseFileItem(shallowCase, urlaubsantragCFIName);
    }
    
    private Layout buildAntragStellenLayout() {
    	VerticalLayout layout = new VerticalLayout();
    	Label urlaubsantragStellen = new Label("Urlaubsantragsdaten erfassen (Enter vacation request data)");
    	
    	Label instructions = new Label("Bitte wählen Sie den Antragsteller aus (please select a requester):");
    	comboWorkers.setItemCaptionGenerator(caseWorker -> caseWorker.getLastname() + ", " + caseWorker.getFirstname());
        List<CaseWorker> caseWorkers = cwService.getAllCaseWorkers();
        comboWorkers.setItems(caseWorkers);
    	Label instructions2 = new Label("Bitte geben Sie den gewünschten Zeitraum an (please choose the period of time):");
    	layout.addComponents(urlaubsantragStellen, instructions, comboWorkers, instructions2);

    	HorizontalLayout dateFields = new HorizontalLayout(von, bis, tage);
    	layout.addComponent(dateFields);
    	von.setDateFormat("dd-MM-yyyy");
    	von.setValue(LocalDate.now());
    	von.setRangeStart(LocalDate.now());
    	von.addValueChangeListener( e -> {    		
    		Date convertedDateTime = java.sql.Date.valueOf(e.getValue());
    		urlaubsantrag.getProperty("von").setValue(convertedDateTime.toString());
    	});
    	
    	bis.setDateFormat("dd-MM-yyyy");
    	bis.setValue(LocalDate.now());
    	bis.setRangeStart(LocalDate.now());
    	bis.addValueChangeListener( e -> {
    		Date convertedDateTime = java.sql.Date.valueOf(e.getValue());
    		urlaubsantrag.getProperty("bis").setValue(convertedDateTime.toString());
    	});
    	
    	btnComplete = generateCompleteButton();
    	btnCancel = generateCancelButton();		
		HorizontalLayout btnLayout = new HorizontalLayout(btnComplete, btnCancel);		
		layout.addComponent(btnLayout);
		
		HorizontalLayout navBtnLayout = new HorizontalLayout(generateTaskListButton());
		layout.addComponents(navBtnLayout);
    	return layout;
	}

	private Button generateCancelButton() {
		Button cancelButton = new Button("Abbrechen (Cancel)");
		cancelButton.addClickListener(e -> {
			navigationEvent.fire(new NavigationEvent("task-list"));
		});
		return cancelButton;
	}

	private Button generateCompleteButton() {
		Button completeButton = new Button("Fertig (Done)");
		completeButton.addClickListener(e -> {
			if(entriesValid()) {
		        Long userId = comboWorkers.getSelectedItem().get().getId();
		        urlaubsantrag.getProperty("antragsteller").setValue(userId.toString());
		        
		        Date convertedDateTime = java.sql.Date.valueOf(von.getValue());
		        urlaubsantrag.getProperty("von").setValue(convertedDateTime.toString());
		        convertedDateTime = java.sql.Date.valueOf(bis.getValue());
		        urlaubsantrag.getProperty("bis").setValue(convertedDateTime.toString());
		        try {
		        	int convertedDaysFromTextField = Integer.parseInt(tage.getValue());
			        urlaubsantrag.getProperty("tage").setValue(tage.getValue());
			        
					caseFileService.updateCaseFileItem(urlaubsantrag);
					taskService.transitionTask(taskInfo.getTask(), caseWorkerInfo.getUser(), StageTaskTransitions.complete);
					btnComplete.setEnabled(false);
					btnCancel.setEnabled(false);
		        } catch (NumberFormatException | NullPointerException nfe) {
		        	Notification.show("Bitte geben Sie die Anzahl der Tage korrekt ein.");
		        }

//				Notification.show("TaskInfo: " + taskInfo.getTask().getCaseRef().getId(), Notification.Type.HUMANIZED_MESSAGE);
			} else {
				
			}
		});
		return completeButton;
	}

	private boolean entriesValid() {
		boolean valid = false;
		if(von.isEmpty() || bis.isEmpty()) {
			valid = false;
		} else {
			valid = true;
		}
		return valid;
	}

	private Button generateTaskListButton() {
		Button taskListButton = new Button("Task List");
		taskListButton.addClickListener(e -> {
			navigationEvent.fire(new NavigationEvent("task-list"));
		});
		return taskListButton;
	}
}