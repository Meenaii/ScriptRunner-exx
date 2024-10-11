import com.atlassian.jira.component.ComponentAccessor

def issueManager = ComponentAccessor.issueManager
def issueLinkManager = ComponentAccessor.issueLinkManager

def issuesWithIntersectingLinks = []

def linkedIssues = issueLinkManager.getOutwardLinks(issue.id).collect {it -> //Сбор исходящих задач по типу связи
    it.issueLinkType.id == 11000
    it.destinationObject 
}
  def intersectingIssues = linkedIssues.each { linkedIssue -> //Поиск пересечений связей с исходящей задачей
        def inwardIssues = issueLinkManager.getInwardLinks(linkedIssue.id).findAll{ ln -> 
            if (ln.issueLinkType.id == 11000 && ln.sourceObject.id != issue.id)
            issuesWithIntersectingLinks.add(ln.sourceObject.getKey())
        }
    }

if(issuesWithIntersectingLinks == [])
	return null
else return issuesWithIntersectingLinks.stream() //Вывод ключей задач
						.distinct()
						.collect()
						.toString().substring(1, issuesWithIntersectingLinks.toString().length()-1)
