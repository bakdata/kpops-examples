set shell := ["bash", "-uc"]

_default:
    @just --list --unsorted

apply:
    kubectl apply -f parent-app.yaml -n argocd

delete:
    kubectl delete -f parent-app.yaml -n argocd


pf port="8888":
    nohup kubectl port-forward --namespace infrastructure service/redpanda-console 8888:8080 > port-forward.out &
    nohup kubectl port-forward --namespace argocd service/argocd-server 8080:443 > port-forward.out &

deploy:
    kpops deploy ./word-count --operation-mode argo > ./word-count/argo/wc-pipeline/manifests.yaml

destroy:
    kpops destroy ./word-count --operation-mode argo > ./word-count/argo/wc-pipeline/manifests.yaml

kill-pf:
    ps aux | grep -i kubectl | grep -v grep | awk {'print $2'} | xargs kill