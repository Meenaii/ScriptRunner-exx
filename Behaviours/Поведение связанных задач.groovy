//Данный скрипт не позволяет привязать более одной задачи к типу задачи Загрузка
//а также не повзволяет заполнить значения сроков начала и окончания, не соотсветсвующие выходящие за сроки привязанной задачи

import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.component.ComponentAccessor

if(getActionName() in ["Create Issue", "Create"]) //Обязательность на этапе создания Загрузки
	getFieldById("issuelinks").setRequired(true)
else{
	def startDateCF = getFieldById("customfield_11200")
	def dueDateCF = getFieldById("duedate")

                def linkType = getFieldById("issuelinks-linktype").getValue() as String
                if (linkType == 'Загрузка -> Проект'){
                    def linkedIssues = getFieldById("issuelinks-issues").getValue() as List //указанная связанная задача на Экране редактирования
                    if (!(linkedIssues == [])){
                        if (linkedIssues.size() > 1)
                            getFieldById("issuelinks-issues").setError('Пожалуйста, привяжите только один объект к Загрузке')
                        else{
                            
                     		def issueManager = ComponentAccessor.getIssueManager()
							CustomField startDateCF1 = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(11200)

                            startDateCF.clearError()
                            dueDateCF.clearError()

                            if (startDateCF.getValue() == '')
                                startDateCF.setError('Пожалуйста, укажите дату начала')
                            else startDateCF.clearError()

                            if (dueDateCF.getValue() == '')
                                dueDateCF.setError('Пожалуйста, укажите срок исполнения')
                            else dueDateCF.clearError()

                            if (startDateCF.getValue() != '' && dueDateCF.getValue() != ''){

                                def startDateLoad = startDateCF.getValue() as Date
                                def dueDateLoad = dueDateCF.getValue() as Date
                            
                                getFieldById("issuelinks-issues").clearError()
                                Date startDatePrj = issueManager.getIssueObject("${linkedIssues.get(0)}") ? startDateCF1.getValue(issueManager.getIssueObject("${linkedIssues.get(0)}")) : null
                                Date dueDatePrj = issueManager.getIssueObject("${linkedIssues.get(0)}") ? issueManager.getIssueObject("${linkedIssues.get(0)}").getDueDate() : null


                                def num1 = (startDatePrj != null) ? (startDatePrj.getTime() - startDateLoad.getTime() > 0 ? 1 : 0) : 0
                                def num2 = (dueDatePrj != null) ? (dueDatePrj.getTime() - dueDateLoad.getTime() < 0 ? 2 : 0) : 0

                                switch(num1 + num2){
                                    case 0 : 
                                        startDateCF.clearError()
                                        dueDateCF.clearError()
                                        break
                                    case 1 :
                                        startDateCF.setError("Дата начала Загрузки не может быть раньше даты нача Проекта - ${startDatePrj.format("dd/MM/yyyy")}")
                                        break
                                    case 2 :
                                        dueDateCF.setError("Срок исполнения Загрузки не может быть позже даты окончания Проекта - ${dueDatePrj.format("dd/MM/yyyy")}")
                                        break
                                    case 3 :
                                        startDateCF.setError("Дата начала Загрузки не может быть раньше даты нача Проекта - ${startDatePrj.format("dd/MM/yyyy")}")
                                        dueDateCF.setError("Срок исполнения Загрузки не может быть позже даты окончания Проекта - ${dueDatePrj.format("dd/MM/yyyy")}")
                                        break

                            }
                  		 }
                    }
                }
               else{
               	startDateCF.clearError()
               	dueDateCF.clearError()
               }
           }
      else{
        startDateCF.clearError()
       	dueDateCF.clearError()
		}
}
