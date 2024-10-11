import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.history.ChangeItemBean

def changeHistoryManager = ComponentAccessor.getChangeHistoryManager()

def inProgressName = issue.getStatus().getName() //текущий статус задачи

def createdTimeDiff = System.currentTimeMillis() - issue.created.time //разница текущего времени и времени создания задачи
Date created = issue.getCreated()
def totalStatusTime = [0L]
def index = 0 
def firstStatus = "" //переменная для первого статуса в бп
def changeItems = changeHistoryManager.getChangeItemsForField(issue, "status") //журнал истории изменения поля "статус"
    if(changeItems.size() == 0) //если история измненеий поля "статус" пуста, то время задачи в статусе = текущее время - время создания
     return ((System.currentTimeMillis() - created.getTime())/ 1000/24/3600).round()

changeItems.each { ChangeItemBean item -> item.fromString

    def timeDiff = System.currentTimeMillis() - item.created.time //разница текущего времени и времени создания элемента в журнале изменений поля "статус"
 	
    if (index == 0)
  		firstStatus = item.fromString //первый статус в бп
    
    if (index == 0 && inProgressName == firstStatus)
    	totalStatusTime << createdTimeDiff // время первого статуса до перехода
    
    if (item.fromString == inProgressName && item.fromString != item.toString) {
        totalStatusTime << -timeDiff //вычитаемое время после смены на текущий статус 
    }
 
  
    if (item.toString == inProgressName) {
        totalStatusTime << timeDiff //прибавляемое время после смены текущего статуса
    }
    
    index++
        
}

def total = totalStatusTime.sum() as Long //суммировние всех временных отрезков текущего статуса 
total / 1000/24/3600 as long ?: 0L //перевод в дни
